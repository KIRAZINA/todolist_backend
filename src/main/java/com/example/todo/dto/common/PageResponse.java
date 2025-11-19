package com.example.todo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Wrapper for paginated responses.
 *
 * @param <T> the type of elements in this page
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    @Schema(description = "List of items on the current page", required = true)
    private List<T> content;

    @Schema(description = "Current page number (zero-based)", example = "0")
    private int pageNumber;

    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;

    @Schema(description = "Total number of items across all pages", example = "42")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @Schema(description = "Indicates whether this is the last page", example = "false")
    private boolean last;
}