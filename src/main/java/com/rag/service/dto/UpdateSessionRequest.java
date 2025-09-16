package com.rag.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request DTO for updating a chat session")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSessionRequest {
    @Schema(description = "New name for the session", example = "Updated Project Discussion")
    private String name;

    @Schema(description = "New favorite status", example = "true")
    private Boolean favorite;

    @Schema(description = "User ID for authorization", example = "123e4567-e89b-12d3-a456-426614174999")
    private String userId;
}
