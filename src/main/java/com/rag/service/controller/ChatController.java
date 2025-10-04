package com.rag.service.controller;

import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.ChatSessionResponse;
import com.rag.service.dto.CreateSessionRequest;
import com.rag.service.dto.PageableResponse;
import com.rag.service.dto.UpdateSessionRequest;
import com.rag.service.service.ChatService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat Sessions", description = "Operations for managing chat sessions and messages")
public class ChatController {
    private final ChatService chatService;
    private final Bucket rateLimitBucket;

    @PostMapping("/sessions")
    @Operation(summary = "Create a new chat session", description = "Creates a chat session with the specified name.")
    public ResponseEntity<ChatSessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(chatService.createSession(request, rateLimitBucket));
    }

    @PutMapping("/sessions/{sessionId}")
    @Operation(summary = "Update a chat session", description = "Updates the name or favorite status of a chat session.")
    public ResponseEntity<ChatSessionResponse> updateSession(
            @Parameter(description = "Session ID", required = true)
            @PathVariable String sessionId,
            @Valid @RequestBody UpdateSessionRequest request) {
        return ResponseEntity.ok(chatService.updateSession(sessionId, request.getName(), request.getFavorite(), request.getUserId(), rateLimitBucket));
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get chat sessions", description = "Returns a paginated list of chat sessions. Optionally filter by favorite.")
    public ResponseEntity<Page<ChatSessionResponse>> getSessions(
            @RequestParam String userId,
            @RequestParam(required = false) Boolean favorite,
            Pageable pageable) {
        Page<ChatSessionResponse> sessions = processSessionsRequest(userId, favorite, pageable);
        return ResponseEntity.ok(sessions);
    }

    private Page<ChatSessionResponse> processSessionsRequest(String userId, Boolean favorite, Pageable pageable) {

        return favorite != null && favorite ?
                chatService.getFavoriteSessions(userId, pageable, rateLimitBucket) :
                chatService.getAllSessions(userId, pageable, rateLimitBucket);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Delete a chat session", description = "Deletes the specified chat session.")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "Session ID", required = true) @PathVariable String sessionId,
            @RequestParam String userId) {
        chatService.deleteSession(sessionId, userId, rateLimitBucket);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Add a message to a chat session", description = "Adds a new message to the specified chat session. Only the session owner can add messages.")
    public ResponseEntity<Void> addMessage(
            @Parameter(description = "Session ID", required = true)
            @PathVariable String sessionId,
            @Valid @RequestBody AddMessageRequest request) {
        chatService.saveMessage(sessionId,request, rateLimitBucket);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Get messages for a chat session", description = "Returns a paginated list of messages for the specified chat session.")
    public ResponseEntity<PageableResponse> getMessages(
            @Parameter(description = "Session ID", required = true)
            @PathVariable String sessionId,
            Pageable pageable) {
        return ResponseEntity.ok(chatService.getSessionMessages(sessionId, pageable, rateLimitBucket));
    }
}
