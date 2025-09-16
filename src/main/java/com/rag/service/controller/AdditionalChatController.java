package com.rag.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.Message;
import com.rag.service.service.SessionChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/session-chat")
@RequiredArgsConstructor
@Tag(name = "Session Chat Approach 2", description = "Additional chat operations for session/message management")
public class AdditionalChatController {
    private final SessionChatService service;

    @GetMapping("/{sessionId}/messages")
    @Operation(
        summary = "Get paginated messages",
        description = "Retrieves messages for a specific session with pagination support"
    )
    public ResponseEntity<List<Message>> getMessages(
            @Parameter(description = "Session ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getPaginatedMessages(sessionId, page, size));
    }

    @PostMapping("{sessionId}/add-message")
    @Operation(
        summary = "Add message to session",
        description = "Adds a new message to an existing chat session"
    )
    public ResponseEntity<Void> addMessage(
            @Parameter(description = "Session ID", required = true) @PathVariable String sessionId,
            @Valid @RequestBody AddMessageRequest req) throws JsonProcessingException {
        service.addMessage(sessionId,req);
        return ResponseEntity.ok().build();
    }
}
