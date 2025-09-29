package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

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

        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1,task2));
        Pageable pageable = PageRequest.of(0,10);

        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);

        Page<Task> result = taskService.getAllTasks(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("task1", result.getContent().get(0).getTitle());

        verify(taskRepository, times(1)).findAll(pageable);
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

        assertTrue(ex.getMessage().contains("Task not found with id: 1"));
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

        verify(taskRepository).save(argThat(task -> task.getTitle().equals("new") && task.getStatus().equals("done")));
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    void deleteTask_ShouldDelete_WhenExists(){
        Task existing = new Task(1L, "task", "description", "open", LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(existing);
    }

    @Test
    void deleteTask_ShouldThrow_WhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L));

        assertTrue(ex.getMessage().contains("Task not found for id: 1"));
    }

    @Test
    void countTasksByStatus_ShouldReturnCorrectCount() {
        when(taskRepository.countByStatus("open")).thenReturn(2L);

        long count = taskService.countTasksByStatus("open");

        assertEquals(2L, count);
    }

    @Test
    void updateStatus_ShouldUpdateStatus_WhenTaskExists() {
        Task task = new Task(1L, "task", "description", "open", LocalDate.now());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.updateStatus(1L, "done");

        assertEquals("done", updated.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    void getTasks_ShouldUseSearch_WhenSearchProvided() {
        Task task = new Task(1L, "titleMatch", "descriptionMatch", "open", LocalDate.now());
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        when(taskRepository.findByTitleOrDescription(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<Task> result = taskService.getTasks(null, null, "titleMatch", pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByTitleOrDescription("titleMatch", "titleMatch", pageable);
    }

    @Test
    void getTasks_ShouldUseStatus_WhenStatusProvided() {
        Task task = new Task(1L, "task", "desc", "open", LocalDate.now());
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        when(taskRepository.findByStatus("open", pageable)).thenReturn(page);

        Page<Task> result = taskService.getTasks("open", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByStatus("open", pageable);
    }

    @Test
    void getTasks_ShouldUseDueDate_WhenDueDateProvided() {
        Task task = new Task(1L, "task", "desc", "open", LocalDate.now().plusDays(1));
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        LocalDate dueDate = LocalDate.now().plusDays(2);
        when(taskRepository.findByDueDateBefore(dueDate, pageable)).thenReturn(page);

        Page<Task> result = taskService.getTasks(null, dueDate, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByDueDateBefore(dueDate, pageable);
    }

    @Test
    void getTasks_ShouldReturnAll_WhenNoFilters() {
        Task task = new Task(1L, "task", "desc", "open", LocalDate.now());
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        when(taskRepository.findAll(pageable)).thenReturn(page);

        Page<Task> result = taskService.getTasks(null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    void findTasksByDueDateAfter_ShouldReturnTasks() {
        Task task = new Task(1L, "task", "desc", "open", LocalDate.now().plusDays(1));
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        LocalDate date = LocalDate.now();
        when(taskRepository.findByDueDateAfter(date, pageable)).thenReturn(page);

        Page<Task> result = taskService.findTasksByDueDateAfter(date, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByDueDateAfter(date, pageable);
    }

    @Test
    void findTasksByDueDateBetween_ShouldReturnTasks() {
        Task task = new Task(1L, "task", "desc", "open", LocalDate.now().plusDays(1));
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(5);
        when(taskRepository.findByDueDateBetween(start, end, pageable)).thenReturn(page);

        Page<Task> result = taskService.findTasksByDueDateBetween(start, end, pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByDueDateBetween(start, end, pageable);
    }

    @Test
    void deleteByDueDateBefore_ShouldCallRepository() {
        LocalDate date = LocalDate.now();
        doNothing().when(taskRepository).deleteByDueDateBefore(date);

        taskService.deleteByDueDateBefore(date);

        verify(taskRepository).deleteByDueDateBefore(date);
    }

    @Test
    void findByTitleContainingIgnoreCase_ShouldReturnTasks() {
        Task task = new Task(1L, "TitleMatch", "desc", "open", LocalDate.now());
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);

        when(taskRepository.findByTitleContainingIgnoreCase("title",pageable)).thenReturn(page);

        Page<Task> result = taskService.findByTitleContainingIgnoreCase("title", pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByTitleContainingIgnoreCase("title", pageable);
    }

    @Test
    void findByDescriptionContainingIgnoreCase_ShouldReturnTasks() {
        Task task = new Task(1L, "task", "DescMatch", "open", LocalDate.now());
        Page<Task> page = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0,10);
        when(taskRepository.findByDescriptionContainingIgnoreCase("desc", pageable)).thenReturn(page);

        Page<Task> result = taskService.findByDescriptionContainingIgnoreCase("desc", pageable);

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByDescriptionContainingIgnoreCase("desc", pageable);
    }
}
