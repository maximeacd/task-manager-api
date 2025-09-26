package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository=taskRepository;
    }

    public Page<Task> getTasks(String status, LocalDate dueDate, String search, Pageable pageable){
        if(search !=null && !search.isEmpty()){
            return taskRepository.findByTitleOrDescription(search, search, pageable);
        }
        else if(status!=null){
            return taskRepository.findByStatus(status, pageable);
        }
        else if(dueDate!=null){
            return taskRepository.findByDueDateBefore(dueDate,pageable);
        }
        else{
            return taskRepository.findAll(pageable);
        }
    }

    public Page<Task> getAllTasks(Pageable pageable){
        return taskRepository.findAll(pageable);
    }

    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow(()-> new RuntimeException("Task not found with id: "+id));
    }

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask){
        Task task = getTaskById(id);
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setDueDate(updatedTask.getDueDate());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found for id: "+id));
        taskRepository.delete(task);
    }

    public long countTasksByStatus(String status){
        return taskRepository.countByStatus(status);
    }

    public Page<Task> findTasksByDueDateAfter(LocalDate dueDate, Pageable pageable){
        return taskRepository.findByDueDateAfter(dueDate, pageable);
    }

    public Page<Task> findTasksByDueDateBetween(LocalDate start, LocalDate end, Pageable pageable){
        return taskRepository.findByDueDateBetween(start, end, pageable);
    }

    public void deleteByDueDateBefore(LocalDate dueDate){
        taskRepository.deleteByDueDateBefore(dueDate);
    }

    public Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable){
        return taskRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    public Page<Task> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable){
        return taskRepository.findByDescriptionContainingIgnoreCase(keyword, pageable);
    }

    public Task updateStatus(Long id, String status){
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found for id: "+id));
        task.setStatus(status);
        return taskRepository.save(task);
    }
}
