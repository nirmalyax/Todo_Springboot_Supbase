package com.example.todo.repository;

import com.example.todo.entity.Todo;
import com.example.todo.entity.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Todo entity providing CRUD operations and custom queries.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    /**
     * Find all todos belonging to a specific user.
     * 
     * @param userId The ID of the user
     * @return List of todos for the user
     */
    List<Todo> findByUserId(UUID userId);
    
    /**
     * Find all todos with a specific status belonging to a user.
     * 
     * @param status The status to filter by
     * @param userId The ID of the user
     * @return List of todos matching the criteria
     */
    List<Todo> findByStatusAndUserId(TodoStatus status, UUID userId);
    
    /**
     * Search todos by title or description containing the search term.
     * 
     * @param searchTerm The term to search for
     * @param userId The ID of the user
     * @return List of todos matching the search criteria
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Todo> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, @Param("userId") UUID userId);
    
    /**
     * Find todos with due dates within a specific range.
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @param userId The ID of the user
     * @return List of todos with due dates in the specified range
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.dueDate >= :startDate AND t.dueDate <= :endDate")
    List<Todo> findByDueDateBetweenAndUserId(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        @Param("userId") UUID userId);
}

