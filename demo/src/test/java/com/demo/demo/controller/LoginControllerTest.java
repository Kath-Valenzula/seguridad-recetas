package com.demo.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.demo.demo.JWTAuthenticationConfig;
import com.demo.demo.models.User;
import com.demo.demo.service.MyUserDetailsService;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setUsername("juan");
        user.setPassword("encodedPass");
        when(userDetailsService.loadUserByUsername("juan")).thenReturn(user);
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        when(jwtAuthenticationConfig.getJWTToken("juan")).thenReturn("token");

        mockMvc.perform(post("/login")
                .param("username", "juan")
                .param("password", "pass"))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginInvalidPassword() throws Exception {
        User user = new User();
        user.setUsername("juan");
        user.setPassword("encodedPass");
        when(userDetailsService.loadUserByUsername("juan")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        mockMvc.perform(post("/login")
                .param("username", "juan")
                .param("password", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        when(userDetailsService.loadUserByUsername("juan")).thenReturn(null);

        mockMvc.perform(post("/login")
                .param("username", "juan")
                .param("password", "pass"))
                .andExpect(status().isUnauthorized());
    }
}
