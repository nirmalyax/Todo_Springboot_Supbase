package com.example.todo.dto;

import com.example.todo.entity.TodoStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating only the status of a Todo item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private TodoStatus status;
}

