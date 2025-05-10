package com.example.todo.dto;

import com.example.todo.entity.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for searching and filtering Todo items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoSearchCriteria {

    private String searchTerm;
    private TodoStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 10;
}

