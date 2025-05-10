package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.TodoSearchCriteria;
import com.example.todo.dto.TodoStatusUpdateRequest;
import com.example.todo.entity.TodoStatus;
import com.example.todo.security.JwtTokenProvider;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller for handling todo-related endpoints.
 */
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class TodoController {

    private final TodoService todoService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new todo.
     *
     * @param todoRequest The todo data
     * @return The created todo
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest todoRequest) {
        log.info("Creating new todo: {}", todoRequest.getTitle());
        
        UUID userId = getCurrentUserId();
        TodoResponse createdTodo = todoService.createTodo(todoRequest, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    /**
     * Get all todos with optional search/filter parameters.
     *
     * @param searchTerm The search term for title or description (optional)
     * @param status The status to filter by (optional)
     * @param fromDate The from date for due date range (optional)
     * @param toDate The to date for due date range (optional)
     * @return List of todos
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TodoResponse>> getAllTodos(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) TodoStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        log.info("Fetching todos with filters - searchTerm: {}, status: {}, dateRange: {} to {}", 
                searchTerm, status, fromDate, toDate);
        
        UUID userId = getCurrentUserId();
        
        TodoSearchCriteria criteria = TodoSearchCriteria.builder()
                .searchTerm(searchTerm)
                .status(status)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        
        List<TodoResponse> todos = todoService.searchTodos(criteria, userId);
        
        return ResponseEntity.ok(todos);
    }

    /**
     * Get a todo by ID.
     *
     * @param id The ID of the todo
     * @return The todo
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable UUID id) {
        log.info("Fetching todo with ID: {}", id);
        
        try {
            UUID userId = getCurrentUserId();
            TodoResponse todo = todoService.getTodoById(id, userId);
            
            return ResponseEntity.ok(todo);
        } catch (IllegalAccessException e) {
            log.warn("Access denied: {}", e.getMessage());
            throw new RuntimeException("Access denied: " + e.getMessage());
        }
    }

    /**
     * Update a todo.
     *
     * @param id The ID of the todo
     * @param todoRequest The updated todo data
     * @return The updated todo
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable UUID id,
            @Valid @RequestBody TodoRequest todoRequest) {
        
        log.info("Updating todo with ID: {}", id);
        
        try {
            UUID userId = getCurrentUserId();
            TodoResponse updatedTodo = todoService.updateTodo(id, todoRequest, userId);
            
            return ResponseEntity.ok(updatedTodo);
        } catch (IllegalAccessException e) {
            log.warn("Access denied: {}", e.getMessage());
            throw new RuntimeException("Access denied: " + e.getMessage());
        }
    }

    /**
     * Update only the status of a todo.
     *
     * @param id The ID of the todo
     * @param statusUpdateRequest The new status
     * @return The updated todo
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TodoResponse> updateTodoStatus(
            @PathVariable UUID id,
            @Valid @RequestBody TodoStatusUpdateRequest statusUpdateRequest) {
        
        log.info("Updating status of todo with ID: {} to {}", id, statusUpdateRequest.getStatus());
        
        try {
            UUID userId = getCurrentUserId();
            TodoResponse updatedTodo = todoService.updateTodoStatus(id, statusUpdateRequest, userId);
            
            return ResponseEntity.ok(updatedTodo);
        } catch (IllegalAccessException e) {
            log.warn("Access denied: {}", e.getMessage());
            throw new RuntimeException("Access denied: " + e.getMessage());
        }
    }

    /**
     * Delete a todo.
     *
     * @param id The ID of the todo
     * @return No content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID id) {
        log.info("Deleting todo with ID: {}", id);
        
        try {
            UUID userId = getCurrentUserId();
            todoService.deleteTodo(id, userId);
            
            return ResponseEntity.noContent().build();
        } catch (IllegalAccessException e) {
            log.warn("Access denied: {}", e.getMessage());
            throw new RuntimeException("Access denied: " + e.getMessage());
        }
    }

    /**
     * Get todos by status.
     *
     * @param status The status to filter by
     * @return List of todos with the specified status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TodoResponse>> getTodosByStatus(@PathVariable TodoStatus status) {
        log.info("Fetching todos with status: {}", status);
        
        UUID userId = getCurrentUserId();
        List<TodoResponse> todos = todoService.getTodosByStatus(status, userId);
        
        return ResponseEntity.ok(todos);
    }

    /**
     * Get todos by due date range.
     *
     * @param fromDate The from date
     * @param toDate The to date
     * @return List of todos with due dates in the range
     */
    @GetMapping("/due-date")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TodoResponse>> getTodosByDueDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        log.info("Fetching todos with due date between: {} and {}", fromDate, toDate);
        
        UUID userId = getCurrentUserId();
        List<TodoResponse> todos = todoService.getTodosByDueDateRange(fromDate, toDate, userId);
        
        return ResponseEntity.ok(todos);
    }

    /**
     * Get the current user's ID from the security context.
     *
     * @return The user ID
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = (String) authentication.getCredentials();
        return jwtTokenProvider.getUserIdFromToken(jwt);
    }
}

