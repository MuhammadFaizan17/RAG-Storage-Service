package com.rag.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request DTO for adding a new message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageRequest {
    @NotBlank(message = "Message content is required")
    @Schema(description = "Content of the message", example = "Hello, team!")
    private String content;

    @NotBlank(message = "Sender is required")
    @Schema(description = "Sender of the message", example = "user")
    private String sender;

    @Schema(description = "Optional context for the message", example = "Previous discussion context")
    private String context;

    @NotBlank(message = "User ID is required")
    @Schema(description = "ID of the user adding the message", example = "123e4567-e89b-12d3-a456-426614174000")
    private String userId;
}
