package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@Testcontainers
public class UserServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldEncodePasswordAndSave(){
        User user = new User();
        user.setUsername("Maxime");
        user.setPassword("mypassword");

        when(passwordEncoder.encode("mypassword")).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> (User) invocation.getArgument(0));

        User savedUser = userService.createUser(user);

        assertEquals("hashedpassword", savedUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrow_WhenUsernameExists(){
        User user = new User();
        user.setUsername("maxime");
        user.setPassword("mypassword");

        when(userRepository.existsByUsername("maxime")).thenReturn(true);
        
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(user));

        assertEquals("Username already exist", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenFound(){
        User user= new User();
        user.setUsername("maxime");

        when(userRepository.findByUsername("maxime")).thenReturn(Optional.of(user));

        Optional<User> found = userRepository.findByUsername("maxime");

        assertTrue(found.isPresent());
        assertEquals("maxime", found.get().getUsername());
    }

    @Test
    void getUserByUsername_ShouldReturnEmpty_WhenNotFound(){
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("ghost");

        assertFalse(result.isPresent());
    }

    @Test
    void checkPassword_ShouldReturnTrue_WhenPasswordMatches(){
        when(passwordEncoder.matches("my","hashed")).thenReturn(true);

        boolean result = userService.checkPassword("my", "hashed");

        assertTrue(result);
    }

    @Test
    void checkPassword_ShouldReturnFalse_WhenPasswordDoesNotMatch(){
        when(userService.checkPassword("my","hashed")).thenReturn(false);

        boolean result = userService.checkPassword("my", "hashed");

        assertFalse(result);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers(){
        User user1 = new User();
        user1.setUsername("maxime");

        User user2 = new User();
        user2.setUsername("max");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1,user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists(){
        User user = new User();
        user.setId(1L);
        user.setUsername("maxime");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals("maxime", result.getUsername());
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));

        assertTrue(ex.getMessage().contains("User not found with id1"));
    }


}
