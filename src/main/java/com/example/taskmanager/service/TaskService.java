package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository=taskRepository;
        this.userRepository=userRepository;
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

    public Page<Task> getTasksForUser(String username, Pageable pageable){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return taskRepository.findByUser(user, pageable);
    }

    public Page<Task> getAllTasks(Pageable pageable){
        return taskRepository.findAll(pageable);
    }

    public Task getTaskById(Long id){
        return taskRepository.findById(id).orElseThrow(()-> new RuntimeException("Task not found with id: "+id));
    }

    public Task createTask(Task task, String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        task.setUser(user);

        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("TO_BE_DONE");
        }
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
        User existingUser = task.getUser();
        if(existingUser == null){
            throw new RuntimeException("Task has no associated user");
        }
        task.setStatus(status);
        task.setUser(existingUser);
        return taskRepository.save(task);
    }
}
