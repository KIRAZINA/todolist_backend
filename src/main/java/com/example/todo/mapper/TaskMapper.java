package com.example.todo.mapper;

import com.example.todo.dto.task.TaskCreateRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.dto.task.TaskUpdateRequest;
import com.example.todo.entity.Task;
import org.mapstruct.*;

/**
 * Mapper for {@link Task} entity ↔ DTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    /**
     * Convert Task entity to TaskResponse DTO.
     */
    TaskResponse toResponse(Task task);

    /**
     * Convert TaskCreateRequest to Task entity.
     * Ignores user and audit fields — they will be set manually.
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priority", source = "priority", qualifiedByName = "stringToPriority")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    Task toEntity(TaskCreateRequest request);

    /**
     * Update existing Task entity from TaskUpdateRequest.
     * Only updates fields that are not null.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(TaskUpdateRequest request, @MappingTarget Task task);

    @Named("stringToPriority")
    default Task.Priority stringToPriority(String value) {
        return value == null ? null : Task.Priority.valueOf(value.toUpperCase());
    }

    @Named("stringToStatus")
    default Task.Status stringToStatus(String value) {
        return value == null ? Task.Status.TODO : Task.Status.valueOf(value.toUpperCase());
    }
}