package ru.ertegix.ates.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.ertegix.ates.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.ertegix.ates.model.AuthRequest;
import ru.ertegix.ates.model.AuthResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import ru.ertegix.ates.security.jwt.JwtTokenProvider;

@Slf4j
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public @ResponseBody AuthResponse login(@RequestBody AuthRequest request) {
        try {
            var username = request.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));
            var user = userRepository.getByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException(username);
            }

            var token = jwtTokenProvider.createToken(user.getPublicId(), username, user.getRole());
            return new AuthResponse(username, token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }
}
