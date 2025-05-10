package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.TodoSearchCriteria;
import com.example.todo.dto.TodoStatusUpdateRequest;
import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoStatus;
import com.example.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the TodoService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    @Transactional
    public TodoResponse createTodo(TodoRequest todoRequest, UUID userId) {
        log.info("Creating new todo for user: {}", userId);
        
        Todo todo = Todo.builder()
                .title(todoRequest.getTitle())
                .description(todoRequest.getDescription())
                .dueDate(todoRequest.getDueDate())
                .status(todoRequest.getStatus())
                .userId(userId)
                .build();
        
        Todo savedTodo = todoRepository.save(todo);
        log.info("Todo created with ID: {}", savedTodo.getId());
        
        return mapToTodoResponse(savedTodo);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse getTodoById(UUID todoId, UUID userId) throws IllegalAccessException {
        log.info("Fetching todo with ID: {} for user: {}", todoId, userId);
        
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    log.warn("Todo not found with ID: {}", todoId);
                    return new IllegalArgumentException("Todo not found with ID: " + todoId);
                });
        
        verifyOwnership(todo, userId);
        
        return mapToTodoResponse(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodosByUserId(UUID userId) {
        log.info("Fetching all todos for user: {}", userId);
        
        List<Todo> todos = todoRepository.findByUserId(userId);
        log.info("Found {} todos for user: {}", todos.size(), userId);
        
        return todos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TodoResponse updateTodo(UUID todoId, TodoRequest todoRequest, UUID userId) throws IllegalAccessException {
        log.info("Updating todo with ID: {} for user: {}", todoId, userId);
        
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    log.warn("Todo not found with ID: {}", todoId);
                    return new IllegalArgumentException("Todo not found with ID: " + todoId);
                });
        
        verifyOwnership(todo, userId);
        
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setDueDate(todoRequest.getDueDate());
        todo.setStatus(todoRequest.getStatus());
        
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Todo updated: {}", updatedTodo.getId());
        
        return mapToTodoResponse(updatedTodo);
    }

    @Override
    @Transactional
    public TodoResponse updateTodoStatus(UUID todoId, TodoStatusUpdateRequest statusUpdateRequest, UUID userId) throws IllegalAccessException {
        log.info("Updating status of todo with ID: {} to {} for user: {}", todoId, statusUpdateRequest.getStatus(), userId);
        
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    log.warn("Todo not found with ID: {}", todoId);
                    return new IllegalArgumentException("Todo not found with ID: " + todoId);
                });
        
        verifyOwnership(todo, userId);
        
        todo.setStatus(statusUpdateRequest.getStatus());
        
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Todo status updated: {}", updatedTodo.getId());
        
        return mapToTodoResponse(updatedTodo);
    }

    @Override
    @Transactional
    public void deleteTodo(UUID todoId, UUID userId) throws IllegalAccessException {
        log.info("Deleting todo with ID: {} for user: {}", todoId, userId);
        
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    log.warn("Todo not found with ID: {}", todoId);
                    return new IllegalArgumentException("Todo not found with ID: " + todoId);
                });
        
        verifyOwnership(todo, userId);
        
        todoRepository.delete(todo);
        log.info("Todo deleted: {}", todoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> searchTodos(TodoSearchCriteria criteria, UUID userId) {
        log.info("Searching todos for user: {} with criteria: {}", userId, criteria);
        
        List<Todo> todos;
        
        if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
            todos = todoRepository.searchByTitleOrDescription(criteria.getSearchTerm(), userId);
        } else if (criteria.getStatus() != null) {
            todos = todoRepository.findByStatusAndUserId(criteria.getStatus(), userId);
        } else if (criteria.getFromDate() != null && criteria.getToDate() != null) {
            todos = todoRepository.findByDueDateBetweenAndUserId(criteria.getFromDate(), criteria.getToDate(), userId);
        } else {
            todos = todoRepository.findByUserId(userId);
        }
        
        log.info("Found {} todos matching criteria for user: {}", todos.size(), userId);
        
        return todos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByStatus(TodoStatus status, UUID userId) {
        log.info("Fetching todos with status: {} for user: {}", status, userId);
        
        List<Todo> todos = todoRepository.findByStatusAndUserId(status, userId);
        log.info("Found {} todos with status {} for user: {}", todos.size(), status, userId);
        
        return todos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByDueDateRange(LocalDateTime startDate, LocalDateTime endDate, UUID userId) {
        log.info("Fetching todos with due date between: {} and {} for user: {}", startDate, endDate, userId);
        
        List<Todo> todos = todoRepository.findByDueDateBetweenAndUserId(startDate, endDate, userId);
        log.info("Found {} todos with due date in range for user: {}", todos.size(), userId);
        
        return todos.stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Verify that the user owns the todo.
     *
     * @param todo The todo
     * @param userId The ID of the user
     * @throws IllegalAccessException if the user does not own the todo
     */
    private void verifyOwnership(Todo todo, UUID userId) throws IllegalAccessException {
        if (!todo.getUserId().equals(userId)) {
            log.warn("User {} tried to access todo {} owned by {}", userId, todo.getId(), todo.getUserId());
            throw new IllegalAccessException("You do not have permission to access this todo");
        }
    }

    /**
     * Map a Todo entity to a TodoResponse DTO.
     *
     * @param todo The Todo entity
     * @return The TodoResponse DTO
     */
    private TodoResponse mapToTodoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .status(todo.getStatus())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }
}


