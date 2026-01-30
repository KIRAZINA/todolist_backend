package com.example.todo.repository;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA repository for {@link Task} entity.
 * Supports pagination, filtering, and bulk operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks belonging to a specific user with pagination.
     */
    Page<Task> findByUser(User user, Pageable pageable);

    /**
     * Find tasks by user and status.
     */
    Page<Task> findByUserAndStatus(User user, Task.Status status, Pageable pageable);

    /**
     * Find tasks due on a specific date for a user.
     */
    List<Task> findByUserAndDueDate(User user, LocalDate dueDate);

    /**
     * Find all tasks due on a specific date (for scheduler/reminders).
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate = :date")
    List<Task> findByDueDate(@Param("date") LocalDate date);

    /**
     * Bulk delete all completed tasks for a user.
     */
    @Modifying
    @Query("DELETE FROM Task t WHERE t.user = :user AND t.status = 'DONE'")
    void deleteAllCompletedByUser(User user);
}