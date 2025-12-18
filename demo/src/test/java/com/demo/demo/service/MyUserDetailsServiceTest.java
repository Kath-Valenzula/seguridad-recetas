package com.demo.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.demo.demo.models.User;
import com.demo.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @Test
    void testLoadUserByUsername() {
        User user = new User();
        user.setUsername("juan");
        when(userRepository.findByUsernameOrEmail("juan", "juan")).thenReturn(user);

        UserDetails result = myUserDetailsService.loadUserByUsername("juan");

        assertEquals("juan", result.getUsername());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsernameOrEmail("juan", "juan")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("juan"));
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = myUserDetailsService.passwordEncoder();
        assertNotNull(encoder);
    }
}
