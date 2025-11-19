package com.example.todo.mapper;

import com.example.todo.dto.user.UserResponse;
import com.example.todo.entity.User;
import org.mapstruct.*;

/**
 * Mapper for {@link User} entity ↔ DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO.
     */
    UserResponse toResponse(User user);

    /**
     * Convert list of users to list of responses.
     */
    // List<UserResponse> toResponseList(List<User> users);
}