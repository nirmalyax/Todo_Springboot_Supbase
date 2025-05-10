package com.example.todo.service;

import com.example.todo.dto.AuthResponse;
import com.example.todo.dto.UserLoginRequest;
import com.example.todo.dto.UserRegistrationRequest;
import com.example.todo.dto.UserResponse;

import java.util.UUID;

/**
 * Service interface for User operations.
 */
public interface UserService {

    /**
     * Register a new user.
     *
     * @param registrationRequest The user registration data
     * @return The user response
     */
    UserResponse registerUser(UserRegistrationRequest registrationRequest);

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param loginRequest The login credentials
     * @return Authentication response with JWT token
     */
    AuthResponse authenticateUser(UserLoginRequest loginRequest);

    /**
     * Get a user by ID.
     *
     * @param userId The ID of the user
     * @return The user response
     */
    UserResponse getUserById(UUID userId);

    /**
     * Check if a username is available.
     *
     * @param username The username to check
     * @return true if the username is available, false otherwise
     */
    boolean isUsernameAvailable(String username);

    /**
     * Check if an email is available.
     *
     * @param email The email to check
     * @return true if the email is available, false otherwise
     */
    boolean isEmailAvailable(String email);
}

