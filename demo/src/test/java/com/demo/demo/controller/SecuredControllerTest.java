package com.demo.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SecuredControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private SecuredController securedController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(securedController).build();
    }

    @Test
    void testGreetings() throws Exception {
        mockMvc.perform(get("/greetings"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello {World}"));
    }

    @Test
    void testGreetingsWithName() throws Exception {
        mockMvc.perform(get("/greetings").param("name", "Juan"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello {Juan}"));
    }
}
