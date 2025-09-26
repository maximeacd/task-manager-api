package com.example.taskmanager.service;

import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder= passwordEncoder;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found with id: "+id));
    }

    public User createUser(User user){
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username already exist");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword){
        return passwordEncoder.matches(rawPassword,encodedPassword);
    }

    public void registerUser(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(request.getRoles());
        userRepository.save(user);
    }

    public void deleteUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found for id: "+id));
        userRepository.delete(user);
    }

    public long countUsers(){
        return userRepository.count();
    }

    public boolean isUsernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    public void deleteUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found for username: "+username));
        userRepository.delete(user);
    }

    public User updatePassword(String username, String newPassword){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found for username: "+username));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
