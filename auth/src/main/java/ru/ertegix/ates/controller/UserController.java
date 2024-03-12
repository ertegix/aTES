package ru.ertegix.ates.controller;

import ru.ertegix.ates.UserEvent;
import ru.ertegix.ates.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ertegix.ates.AtesAuthAppConstants;
import ru.ertegix.ates.model.CreateUserRequest;
import ru.ertegix.ates.model.Role;
import ru.ertegix.ates.model.User;
import ru.ertegix.ates.queue.MessageSender;
import ru.ertegix.ates.util.IdGenerator;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final IdGenerator idGenerator = new IdGenerator();

    private final UserRepository userRepository;
    private final MessageSender messageSender;

    @PostMapping(path = "/register")
    public @ResponseBody User register(@RequestBody CreateUserRequest request) {
        var user = User
                .builder()
                .id(idGenerator.getNext())
                .publicId(UUID.randomUUID())
                .username(request.getUsername())
                .password(request.getPassword())
                .role(Role.WORKER.name())
                .build();

        var registeredUser = userRepository.saveAndFlush(user);

        var event = new UserEvent(
                registeredUser.getPublicId().toString(),
                registeredUser.getUsername(),
                registeredUser.getRole()
        );
        messageSender.sendMessage(event, AtesAuthAppConstants.USER_TOPIC_NAME);
        return registeredUser;
    }

    @GetMapping("/all")
    public @ResponseBody List<User> getAll() {
        return userRepository.findAll();
    }
}
