package com.example.taskmanager.controller;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties="spring.security.enabled=false")
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

        mockMvc.perform(get("/api/users/{id}", user.getId())
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maxime"));
    }

    @Test
    void getUserById_ShouldReturnNotFound() throws Exception{
        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/{id}", 999L)
                .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByUsername_ShouldReturnUser() throws Exception {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("pass");
        userRepository.save(user);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/by-username/{username}", "alice")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getUserByUsername_ShouldReturnNull() throws Exception {
        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/by-username/{username}", "bob")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));
    }


    @Test
    void deleteUserById_ShouldReturnNoContent() throws Exception {
        User user = new User();
        user.setUsername("maxime");
        user.setPassword("pass");
        user = userRepository.save(user);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(delete("/api/users/{id}", user.getId())
                        .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserById_ShouldReturnNotFound() throws Exception {
        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(delete("/api/users/{id}", 999L)
                        .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void countUsers_ShouldReturnCorrectNumber() throws Exception {
        User user1 = new User(); user1.setUsername("u1"); user1.setPassword("p1");
        User user2 = new User(); user2.setUsername("u2"); user2.setPassword("p2");
        userRepository.save(user1);
        userRepository.save(user2);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/count")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void isUsernameExists_ShouldReturnTrueOrFalse() throws Exception {
        User user = new User(); user.setUsername("existing"); user.setPassword("pass");
        userRepository.save(user);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(get("/api/users/exists").param("username", "existing")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/api/users/exists").param("username", "notfound")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void deleteUserByUsername_ShouldReturnNoContent() throws Exception {
        User user = new User(); user.setUsername("toDelete"); user.setPassword("pass");
        userRepository.save(user);

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(delete("/api/users/by-username").param("username", "toDelete")
                        .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserByUsername_ShouldReturnNotFound() throws Exception {
        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(delete("/api/users/by-username").param("username", "unknown")
                        .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePassword_ShouldReturnNotFound() throws Exception {
        String newPasswordJson = objectMapper.writeValueAsString(new UserController.UpdatePasswordRequest() {{
            setNewPassword("newpass");
        }});

        String jwtToken = "Bearer " + jwtUtil.generationToken("testuser");

        mockMvc.perform(patch("/api/users/{username}/password", "unknown")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPasswordJson))
                .andExpect(status().isNotFound());
    }
}
