package com.example.taskmanager.controller;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService=taskService;
    }

    @GetMapping("/all")
    public Page<Task> getAllTasks(Pageable pageable){
        return taskService.getAllTasks(pageable);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody Task task){
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody Task task){
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
    }

    @GetMapping
    public Page<Task> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate dueDate,
            @RequestParam(required = false) String search){
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page,size, sort);
        return taskService.getTasks(status, dueDate, search, pageable);
    }

    @GetMapping("/status")
    public long countTasksByStatus(@RequestParam String status){
        return taskService.countTasksByStatus(status);
    }

    @GetMapping("/due-after")
    public Page<Task> findTasksByDueDateAfter(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate, Pageable pageable){
        return taskService.findTasksByDueDateAfter(dueDate, pageable);
    }

    @GetMapping("/due-between")
    public Page<Task> findTasksByDueDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Pageable pageable){
        return taskService.findTasksByDueDateBetween(start, end, pageable);
    }

    @DeleteMapping("/due-before")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByDueDateBefore(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate){
        taskService.deleteByDueDateBefore(dueDate);
    }

    @GetMapping("/search/title")
    public Page<Task> findByTitleContainingIgnoreCase(@RequestParam String keyword, Pageable pageable){
        return taskService.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    @GetMapping("/search/description")
    public Page<Task> findByDescriptionContainingIgnoreCase(@RequestParam String keyword, Pageable pageable){
        return taskService.findByDescriptionContainingIgnoreCase(keyword, pageable);
    }

    @PatchMapping("/{id}/status")
    public Task updateStatus(@PathVariable Long id, @RequestParam String status){
        return taskService.updateStatus(id, status);
    }
}
