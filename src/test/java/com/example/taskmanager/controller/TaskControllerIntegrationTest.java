package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(properties="spring.security.enabled=false")
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        taskRepository.deleteAll();
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus("open");
        task.setDueDate(LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("open"));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        Task task = new Task();
        task.setTitle("Task1");
        task.setDescription("Description1");
        task.setStatus("open");
        task.setDueDate(LocalDate.now().plusDays(2));
        task = taskRepository.save(task);

        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task1"));
    }

    @Test
    void getTaskById_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        Task task = new Task();
        task.setTitle("Old Task");
        task.setDescription("Old Description");
        task.setStatus("open");
        task.setDueDate(LocalDate.now().plusDays(1));
        task = taskRepository.save(task);

        task.setTitle("Updated Task");
        task.setStatus("done");

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("done"));
    }

    @Test
    void deleteTask_ShouldDelete_WhenExists() throws Exception {
        Task task = new Task();
        task.setTitle("Task to delete");
        task.setDescription("Description");
        task.setStatus("open");
        task.setDueDate(LocalDate.now());
        task = taskRepository.save(task);

        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
