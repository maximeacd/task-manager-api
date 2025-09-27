package com.example.taskmanager.controller;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user){
        return userService.createUser(user);
    }

    @GetMapping("/by-username/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username){
        return userService.getUserByUsername(username);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
    }

    @GetMapping("/count")
    public long countUsers(){
        return userService.countUsers();
    }

    @GetMapping("/exists")
    public boolean isUsernameExists(@RequestParam String username){
        return userService.isUsernameExists(username);
    }

    @DeleteMapping("/by-username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserByUsername(@RequestParam String username){
        userService.deleteUserByUsername(username);
    }

    @PatchMapping("/{username}/password")
    public User updatePassword(@PathVariable String username, @RequestBody UpdatePasswordRequest request){
        return userService.updatePassword(username, request.getNewPassword());
    }

    public static class UpdatePasswordRequest{
        private String newPassword;
        public String getNewPassword(){
            return newPassword;
        }
        public void setNewPassword(String newPassword){
            this.newPassword=newPassword;
        }
    }
}
