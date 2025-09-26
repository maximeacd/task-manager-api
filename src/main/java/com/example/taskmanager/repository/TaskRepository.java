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

    long countByStatus(String status);

    Page<Task> findByDueDateBefore(LocalDate dueDate, Pageable pageable);

    Page<Task> findByDueDateAfter(LocalDate dueDate, Pageable pageable);

    Page<Task> findByDueDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    void deleteByDueDateBefore(LocalDate dueDate);

    Page<Task> findByTitleOrDescription(String title, String description, Pageable pageable);

    Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Task> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);
}
