package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
public class TaskServiceTest {

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

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks(){
        Task task1 = new Task(1L, "task1", "description1", "open", LocalDate.now());
        Task task2 = new Task(2L, "task2", "description2", "done", LocalDate.now());

        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists(){
        Task task = new Task(1L, "task", "description", "open", LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertEquals("task", result.getTitle());
    }

    @Test
    void getTaskById_ShouldThrow_WhenNotFound(){
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L));

        assertTrue(ex.getMessage().contains("Task not found with id1"));
    }

    @Test
    void createTask_ShouldSaveAndReturnTask(){
        Task task = new Task(null, "task", "description", "open", LocalDate.now());

        when(taskRepository.save(task)).thenReturn(new Task(1L, "task", "description", "open", LocalDate.now()));

        Task result = taskService.createTask(task);

        assertNotNull(result.getId());
        assertEquals("task", result.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_ShouldUpdatedAndReturnTask(){
        Task existing = new Task(1L, "old", "old", "open", LocalDate.now());
        Task updated = new Task(null, "new", "new", "done", LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer((invocation->invocation.getArgument(0)));

        Task result = taskService.updateTask(1L, updated);

        assertEquals("new", result.getTitle());
        assertEquals("done", result.getStatus());
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    void deleteTask_ShouldDelete_WhenExists(){
        Task existing = new Task(1L, "task", "description", "open", LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }
}
