package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService=taskService;
    }

    @GetMapping("/all")
    public List<Task> getAllTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Task getTaskById(@Valid @PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PostMapping
    public Task createTask(@Valid @RequestBody Task task){
        return taskService.createTask(task);
    }

    @PutMapping
    public Task updateTask(@Valid @PathVariable Long id,@Valid @RequestBody Task task){
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@Valid @PathVariable Long id){
        taskService.deleteTask(id);
    }

    @GetMapping
    public Page<Task> getTasks(
            @Valid @RequestParam(defaultValue = "0") int page,
            @Valid @RequestParam(defaultValue = "10") int size,
            @Valid @RequestParam(defaultValue = "id") String sortBy,
            @Valid @RequestParam(defaultValue = "asc") String sortDir,
            @Valid @RequestParam(required = false) String status,
            @Valid @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate dueDate,
            @Valid @RequestParam(required = false) String search){
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page,size, sort);
        return taskService.getTasks(status, dueDate, search,pageable);
    }
}
