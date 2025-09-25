package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(String status, Pageable pageable);

    Page<Task> findByDueDateBefore(LocalDate dueDate, Pageable pageable);

    Page<Task> findByTitleOrDescription(String title, String description, Pageable pageable);
}
