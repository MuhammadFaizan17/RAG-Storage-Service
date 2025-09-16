package com.rag.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request DTO for creating a new chat session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {
    @NotBlank(message = "Session name is required")
    @Schema(description = "Name of the session", example = "Project Discussion")
    private String name;

    @NotBlank(message = "User ID is required")
    @Schema(description = "ID of the user creating the session", example = "123e4567-e89b-12d3-a456-426614174999")
    private String userId;
}
