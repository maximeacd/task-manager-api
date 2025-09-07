package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    @NotBlank(message="Username cannot be empty")
    @Size(max=50, message="Username must be at most 50 characters")
    private String username;

    @NotBlank(message="Password cannot be empty")
    @Size(min=6, message="Password must be at most 6 characters")
    private String password;

    @NotEmpty(message="Roles cannot be empty")
    private Set<String> roles;
}
