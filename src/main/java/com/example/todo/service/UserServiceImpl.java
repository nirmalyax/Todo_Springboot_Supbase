package com.example.todo.service;

import com.example.todo.dto.AuthResponse;
import com.example.todo.dto.UserLoginRequest;
import com.example.todo.dto.UserRegistrationRequest;
import com.example.todo.dto.UserResponse;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the UserService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest registrationRequest) {
        log.info("Registering new user with username: {}", registrationRequest.getUsername());
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            log.warn("Username {} is already taken", registrationRequest.getUsername());
            throw new IllegalArgumentException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            log.warn("Email {} is already in use", registrationRequest.getEmail());
            throw new IllegalArgumentException("Email is already in use");
        }
        
        // Create new user with encrypted password
        User user = User.builder()
                .username(registrationRequest.getUsername())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .enabled(true)
                .roles(new HashSet<>(Set.of("USER")))
                .build();
        
        // Save the user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse authenticateUser(UserLoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());
        
        try {
            // Authenticate user with Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), 
                            loginRequest.getPassword()
                    )
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details from authentication
            User user = (User) authentication.getPrincipal();
            
            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication, user.getId());
            
            log.info("User authenticated successfully: {}", user.getUsername());
            
            // Return authentication response
            return new AuthResponse(jwt, user.getId(), user.getUsername());
            
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.info("Fetching user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found with ID: " + userId);
                });
        
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        log.info("Checking if username is available: {}", username);
        return !userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        log.info("Checking if email is available: {}", email);
        return !userRepository.existsByEmail(email);
    }

    /**
     * Map a User entity to a UserResponse DTO.
     *
     * @param user The User entity
     * @return The UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .enabled(user.isEnabled())
                .build();
    }
}
