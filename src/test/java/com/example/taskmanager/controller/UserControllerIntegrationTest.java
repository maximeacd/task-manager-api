package com.example.taskmanager.controller;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties="spring.security.enabled=false")
@Disabled("Integration tests disabled for now")
public class UserControllerIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception{
        User user = new User();
        user.setUsername("maxime");
        user.setPassword("mypassword");

        userRepository.save(user);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");


        mockMvc.perform(get("/api/users")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("maxime"));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception{
        User user = new User();
        user.setUsername("maxime");
        user.setPassword("mypassword");

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(post("/api/users")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maxime"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception{
        User user = new User();
        user.setUsername("maxime");
        user.setPassword("mypassword");

        user=userRepository.save(user);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/id/{id}", user.getId())
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maxime"));
    }

    @Test
    void getUserById_ShouldReturnNotFound() throws Exception{
        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/id/{id}", 999L)
                .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }
}
