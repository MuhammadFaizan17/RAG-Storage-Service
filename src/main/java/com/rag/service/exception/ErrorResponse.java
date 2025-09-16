package com.rag.service.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Map<String, String> details;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
