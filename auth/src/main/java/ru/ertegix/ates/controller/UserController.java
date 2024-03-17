package ru.ertegix.ates.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.ertegix.ates.event.UserEvent_v1;
import ru.ertegix.ates.model.ChangeUserRoleRequest;
import ru.ertegix.ates.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ertegix.ates.AtesAuthAppConstants;
import ru.ertegix.ates.model.CreateUserRequest;
import ru.ertegix.ates.model.Role;
import ru.ertegix.ates.model.User;
import ru.ertegix.ates.queue.MessageSender;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserRepository userRepository;
    private final MessageSender messageSender;
    private final BCryptPasswordEncoder passwordEncoder;


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path = "/register")
    public @ResponseBody User register(@RequestBody CreateUserRequest request) {
        User userFound = userRepository.getByUsername(request.getUsername());
        if (userFound != null) {
            throw new IllegalArgumentException("User with same username registerd");
        }

        var user = User
                .builder()
                .publicId(UUID.randomUUID())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.WORKER.name())
                .build();

        var registeredUser = userRepository.saveAndFlush(user);

        var event = new UserEvent_v1(
                registeredUser.getPublicId().toString(),
                registeredUser.getUsername(),
                registeredUser.getRole()
        );
        messageSender.sendMessage(event, AtesAuthAppConstants.USER_CUD_TOPIC_NAME);
        return registeredUser;
    }

    @PutMapping("/changeRole")
    public @ResponseBody User changeRole(@RequestBody ChangeUserRoleRequest request) {
        userRepository.getByPublicId(request.getPublicId())
                .ifPresent(new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        user.setRole(request.getRole().name());
                        userRepository.saveAndFlush(user);
                        messageSender.sendMessage(
                                new UserEvent_v1(
                                        user.getPublicId().toString(),
                                        user.getUsername(),
                                        user.getRole()
                                ),
                                AtesAuthAppConstants.USER_CUD_TOPIC_NAME);
                    }
                });

        return userRepository.getByPublicId(request.getPublicId()).orElse(null);
    }

    @GetMapping("/all")
    public @ResponseBody List<User> getAll() {
        return userRepository.findAll();
    }
}
