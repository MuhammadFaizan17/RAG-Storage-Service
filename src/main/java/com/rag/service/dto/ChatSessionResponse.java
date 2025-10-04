package com.rag.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response DTO for chat session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionResponse {
    @Schema(description = "Session ID")
    private UUID id;

    @Schema(description = "Session name", example = "Project Discussion")
    private String name;

    @Schema(description = "Is session marked as favorite", example = "true")
    private boolean favorite;

    @Schema(description = "Session creation timestamp", example = "2025-09-14T12:34:56Z")
    private LocalDateTime createdAt;

    @Schema(description = "Session update timestamp", example = "2025-09-14T12:34:56Z")
    private LocalDateTime updatedAt;

    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174999")
    private UUID userId;
}
