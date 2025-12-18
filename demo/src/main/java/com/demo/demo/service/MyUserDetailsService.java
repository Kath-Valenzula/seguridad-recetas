package com.demo.demo.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.demo.models.User;
import com.demo.demo.repository.UserRepository;

@Configuration
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user == null) {
            throw new UsernameNotFoundException(usernameOrEmail);
        }
        return user;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
