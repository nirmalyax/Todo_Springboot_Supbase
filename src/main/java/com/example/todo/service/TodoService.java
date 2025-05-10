package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.TodoSearchCriteria;
import com.example.todo.dto.TodoStatusUpdateRequest;
import com.example.todo.entity.TodoStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Todo operations.
 */
public interface TodoService {

    /**
     * Create a new todo for a user.
     *
     * @param todoRequest The todo data
     * @param userId The ID of the user
     * @return The created todo
     */
    TodoResponse createTodo(TodoRequest todoRequest, UUID userId);

    /**
     * Get a todo by ID.
     *
     * @param todoId The ID of the todo
     * @param userId The ID of the user
     * @return The todo
     * @throws IllegalAccessException if the user does not own the todo
     */
    TodoResponse getTodoById(UUID todoId, UUID userId) throws IllegalAccessException;

    /**
     * Get all todos for a user.
     *
     * @param userId The ID of the user
     * @return List of todos
     */
    List<TodoResponse> getAllTodosByUserId(UUID userId);

    /**
     * Update a todo.
     *
     * @param todoId The ID of the todo
     * @param todoRequest The updated todo data
     * @param userId The ID of the user
     * @return The updated todo
     * @throws IllegalAccessException if the user does not own the todo
     */
    TodoResponse updateTodo(UUID todoId, TodoRequest todoRequest, UUID userId) throws IllegalAccessException;

    /**
     * Update the status of a todo.
     *
     * @param todoId The ID of the todo
     * @param statusUpdateRequest The new status
     * @param userId The ID of the user
     * @return The updated todo
     * @throws IllegalAccessException if the user does not own the todo
     */
    TodoResponse updateTodoStatus(UUID todoId, TodoStatusUpdateRequest statusUpdateRequest, UUID userId) throws IllegalAccessException;

    /**
     * Delete a todo.
     *
     * @param todoId The ID of the todo
     * @param userId The ID of the user
     * @throws IllegalAccessException if the user does not own the todo
     */
    void deleteTodo(UUID todoId, UUID userId) throws IllegalAccessException;

    /**
     * Search todos by criteria.
     *
     * @param criteria The search criteria
     * @param userId The ID of the user
     * @return List of todos matching the criteria
     */
    List<TodoResponse> searchTodos(TodoSearchCriteria criteria, UUID userId);

    /**
     * Get todos by status.
     *
     * @param status The status to filter by
     * @param userId The ID of the user
     * @return List of todos with the specified status
     */
    List<TodoResponse> getTodosByStatus(TodoStatus status, UUID userId);

    /**
     * Get todos with due dates in the specified range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @param userId The ID of the user
     * @return List of todos with due dates in the range
     */
    List<TodoResponse> getTodosByDueDateRange(LocalDateTime startDate, LocalDateTime endDate, UUID userId);
}

