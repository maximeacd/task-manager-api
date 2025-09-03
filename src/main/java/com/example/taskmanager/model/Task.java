package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private boolean completed=false;
    private LocalDate dueDate;

    public Task(){
    }

    public Task(String title, String description, boolean completed, LocalDate dueDate){
        this.title=title;
        this.description=description;
        this.completed=completed;
        this.dueDate=dueDate;
    }
}
