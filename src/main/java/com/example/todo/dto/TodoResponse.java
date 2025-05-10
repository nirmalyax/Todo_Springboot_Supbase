package com.example.todo.dto;

import com.example.todo.entity.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for sending Todo data to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TodoStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

