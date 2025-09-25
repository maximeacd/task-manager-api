package com.example.taskmanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max=100, message = "Title must be at most 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(max=500, message = "Description must be at most 500 characters")
    private String description;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    @FutureOrPresent(message="Due date cannot be in the past")
    private LocalDate dueDate;
}
