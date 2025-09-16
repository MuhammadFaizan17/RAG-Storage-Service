package com.rag.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseDto {

    @Schema(description = "Message ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private String id;

    @Schema(description = "Message content", example = "Hello, team!")
    private String content;

    @Schema(description = "Sender of the message", example = "user")
    private String sender;

    @Schema(description = "Context for the message", example = "Project discussion")
    private String context;

    @Schema(description = "Message timestamp", example = "2025-09-14T12:34:56Z")
    private String createdAt;
}
