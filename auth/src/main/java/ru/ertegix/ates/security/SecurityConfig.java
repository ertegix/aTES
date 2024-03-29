package ru.ertegix.ates.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ertegix.ates.model.User;
import ru.ertegix.ates.repository.UserRepository;
import ru.ertegix.ates.security.jwt.JwtTokenFilter;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenFilter jwtTokenFilter;
    private final UserRepository userRepository;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/auth/login"
                        ,"/users/register"
                )
                .permitAll()
//                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @PostConstruct
    public void addAdmin() {
        User user = new User(
                UUID.randomUUID(),
                "admin",
                "$2a$10$D68jwZ8.7m1ynIZmTDiZ2ePPzQldzjF/IzGqlfvnFvPbVpwkLhKL6",
                "ADMIN"
        );

        userRepository.saveAndFlush(user);
    }
}
