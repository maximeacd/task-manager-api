package com.example.taskmanager.controller;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/id/{id}")
    public User getUserById(@Valid @PathVariable Long id){
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user){
        return userService.createUser(user);
    }

    @GetMapping("/username/{username}")
    public Optional<User> getUserByUsername(@Valid @PathVariable String username){
        return userService.getUserByUsername(username);
    }
}
