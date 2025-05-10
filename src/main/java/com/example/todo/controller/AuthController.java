package com.example.todo.controller;

import com.example.todo.dto.AuthResponse;
import com.example.todo.dto.UserLoginRequest;
import com.example.todo.dto.UserRegistrationRequest;
import com.example.todo.dto.UserResponse;
import com.example.todo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

    /**
     * Register a new user.
     *
     * @param registrationRequest The user registration data
     * @return The user response
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        log.info("Received registration request for user: {}", registrationRequest.getUsername());
        
        try {
            UserResponse userResponse = userService.registerUser(registrationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param loginRequest The login credentials
     * @return Authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody UserLoginRequest loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        
        try {
            AuthResponse authResponse = userService.authenticateUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if a username is available.
     *
     * @param username The username to check
     * @return true if the username is available, false otherwise
     */
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@RequestParam String username) {
        log.info("Checking availability for username: {}", username);
        
        boolean isAvailable = userService.isUsernameAvailable(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if an email is available.
     *
     * @param email The email to check
     * @return true if the email is available, false otherwise
     */
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam String email) {
        log.info("Checking availability for email: {}", email);
        
        boolean isAvailable = userService.isEmailAvailable(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        
        return ResponseEntity.ok(response);
    }
}

