package com.example.taskmanager.controller;

import com.example.taskmanager.model.User;
import com.example.taskmanager.security.JwtUtil;
import com.example.taskmanager.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil){
        this.userService=userService;
        this.jwtUtil=jwtUtil;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user){
        User foundUser = userService.getUserByUsername(user.getUsername()).orElseThrow(()-> new RuntimeException("Invalide credentials"));
        if(!userService.checkPassword(user.getPassword(), foundUser.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generationToken(foundUser.getUsername());
        return Map.of("token", token);
    }
}
