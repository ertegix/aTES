package org.ates.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ates.AtesAuthAppConstants;
import org.ates.event.UserCreated;
import org.ates.model.CreateUserRequest;
import org.ates.model.User;
import org.ates.queue.MessageSender;
import org.ates.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserRepository userRepository;
    private final MessageSender messageSender;

    @PostMapping
    public @ResponseBody User register(@RequestBody CreateUserRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        var registeredUser = userRepository.saveAndFlush(user);

        var event = new UserCreated(
                registeredUser.getId(),
                registeredUser.getUsername(),
                ""
        );
        messageSender.sendMessage(event, AtesAuthAppConstants.USER_TOPIC_NAME);
        return registeredUser;
    }

    @GetMapping("/all")
    public @ResponseBody List<User> getAll() {
        return userRepository.findAll();
    }
}
