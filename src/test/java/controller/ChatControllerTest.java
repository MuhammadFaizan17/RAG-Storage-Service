package controller;

import com.rag.service.controller.ChatController;
import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.ChatSessionResponse;
import com.rag.service.dto.CreateSessionRequest;
import com.rag.service.dto.MessageResponseDto;
import com.rag.service.dto.PageableResponse;
import com.rag.service.dto.UpdateSessionRequest;
import com.rag.service.service.ChatService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private Bucket rateLimitBucket;

    @InjectMocks
    private ChatController chatController;

    private ChatSessionResponse mockSessionResponse;
    private UUID userId;
    private UUID sessionId;
    private String userIdStr;
    private String sessionIdStr;
    private Instant now;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        userIdStr = userId.toString();
        sessionIdStr = sessionId.toString();
        now = Instant.now();

        mockSessionResponse = ChatSessionResponse.builder()
                .id(sessionId)
                .name("Test Session")
                .userId(userId)
                .favorite(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createSession_ShouldReturnNewSession() {

        CreateSessionRequest request = CreateSessionRequest.builder()
                .name("Test Session")
                .userId(userIdStr)
                .build();
        when(chatService.createSession(request, rateLimitBucket))
                .thenReturn(mockSessionResponse);


        ResponseEntity<ChatSessionResponse> response = chatController.createSession(request);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSessionResponse, response.getBody());
        verify(chatService).createSession(request, rateLimitBucket);
    }

    @Test
    void updateSession_ShouldReturnUpdatedSession() {

        UpdateSessionRequest request = UpdateSessionRequest.builder()
                .name("Updated Session")
                .favorite(true)
                .userId(userIdStr)
                .build();

        ChatSessionResponse updatedSession = ChatSessionResponse.builder()
                .id(sessionId)
                .name("Updated Session")
                .userId(userId)
                .favorite(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(chatService.updateSession(sessionIdStr, request.getName(), request.getFavorite(),
                request.getUserId(), rateLimitBucket))
                .thenReturn(updatedSession);


        ResponseEntity<ChatSessionResponse> response = chatController.updateSession(sessionIdStr, request);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedSession, response.getBody());
        verify(chatService).updateSession(sessionIdStr, request.getName(), request.getFavorite(),
                request.getUserId(), rateLimitBucket);
    }

    @Test
    void getSessions_ShouldReturnPaginatedSessions() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatSessionResponse> sessionPage = new PageImpl<>(Collections.singletonList(mockSessionResponse));

        when(chatService.getAllSessions(userIdStr, pageable, rateLimitBucket))
                .thenReturn(sessionPage);


        ResponseEntity<Page<ChatSessionResponse>> response = chatController.getSessions(userIdStr, null, pageable);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessionPage, response.getBody());
        verify(chatService).getAllSessions(userIdStr, pageable, rateLimitBucket);
    }

    @Test
    void getFavoriteSessions_ShouldReturnPaginatedFavoriteSessions() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatSessionResponse> sessionPage = new PageImpl<>(Collections.singletonList(mockSessionResponse));

        when(chatService.getFavoriteSessions(userIdStr, pageable, rateLimitBucket))
                .thenReturn(sessionPage);


        ResponseEntity<Page<ChatSessionResponse>> response = chatController.getSessions(userIdStr, true, pageable);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sessionPage, response.getBody());
        verify(chatService).getFavoriteSessions(userIdStr, pageable, rateLimitBucket);
    }

    @Test
    void deleteSession_ShouldReturnOkResponse() {

        ResponseEntity<Void> response = chatController.deleteSession(sessionIdStr, userIdStr);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService).deleteSession(sessionIdStr, userIdStr, rateLimitBucket);
    }

    @Test
    void addMessage_ShouldReturnOkResponse() {

        AddMessageRequest request = AddMessageRequest.builder()
                .content("Test message")
                .userId(userIdStr)
                .build();


        ResponseEntity<?> response = chatController.addMessage(sessionIdStr, request);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(chatService).saveMessage(sessionIdStr, request, rateLimitBucket);
    }

    @Test
    void getMessages_ShouldReturnPaginatedMessages() {

        Pageable pageable = PageRequest.of(0, 10);
        PageableResponse mockResponse = PageableResponse.builder()
                .data(Collections.singletonList(MessageResponseDto.builder()
                        .id(UUID.randomUUID().toString())
                        .content("Test message")
                        .sender("user")
                        .createdAt("2023-01-01")
                        .build()))
                .totalPages(1)
                .totalElements(1L)
                .build();

        when(chatService.getSessionMessages(sessionIdStr, pageable, rateLimitBucket))
                .thenReturn(mockResponse);


        ResponseEntity<PageableResponse> response = chatController.getMessages(sessionIdStr, pageable);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(chatService).getSessionMessages(sessionIdStr, pageable, rateLimitBucket);
    }
}
