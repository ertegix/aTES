package org.ates.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ates.AtesAuthAppConstants;
import org.ates.event.UserCreated;
import org.ates.model.CreateUserRequest;
import org.ates.model.User;
import org.ates.queue.MessageSender;
import org.ates.repository.UserRepository;
import org.ates.util.IdGenerator;
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
                .build();

        var registeredUser = userRepository.saveAndFlush(user);

        var event = new UserCreated(
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
