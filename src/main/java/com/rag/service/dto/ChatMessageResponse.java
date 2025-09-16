package com.rag.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "Response DTO for chat message")
@Data
@Builder
public class ChatMessageResponse {


    @Schema(description = "Session ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String sessionId;

    @Schema(description = "Session Name", example = "Project Discussion")
    private String sessionName;

    private List<MessageResponseDto> messages;


}
