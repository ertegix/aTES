package org.ates.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ates.model.AuthRequest;
import org.ates.model.AuthResponse;
import org.ates.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

//    private final AuthenticationManager authManager;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public @ResponseBody AuthResponse login(@RequestBody AuthRequest request) {
        try {
            var login = request.getUsername();
            var token = "ololo";
            return new AuthResponse(login, token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }
}
