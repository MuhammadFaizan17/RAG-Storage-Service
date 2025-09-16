package service;

import com.rag.service.dto.ChatSessionResponse;
import com.rag.service.entity.ChatSession;
import com.rag.service.entity.User;
import com.rag.service.exception.NotFoundException;
import com.rag.service.exception.RateLimitException;
import com.rag.service.repository.ChatSessionRepository;
import com.rag.service.repository.UserRepository;
import com.rag.service.service.ChatService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock ChatSessionRepository sessionRepository;
    @Mock UserRepository userRepository;
    @Mock Bucket rateLimitBucket;
    @InjectMocks ChatService chatService;

    private UUID userId;
    private UUID sessionId;
    private User user;
    private ChatSession session;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        user = User.builder().id(userId).name("Test User").email("test@example.com").build();
        session = ChatSession.builder().id(sessionId).name("Session").user(user).favorite(false).build();
    }

    @Test
    void createSession_ShouldCreateSession() {
        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.existsByNameAndUser("Session", user)).thenReturn(false);
        when(sessionRepository.save(any(ChatSession.class))).thenReturn(session);

        ChatSessionResponse response = chatService.createSession("Session", userId.toString(), rateLimitBucket);
        assertNotNull(response);
        assertEquals(sessionId, response.getId());
        assertEquals("Session", response.getName());
    }

    @Test
    void createSession_ShouldThrowRateLimitException() {
        when(rateLimitBucket.tryConsume(1)).thenReturn(false);
        Executable executable = () ->  chatService.createSession("Session", userId.toString(), rateLimitBucket);
        assertThrows(RateLimitException.class, executable);
    }

    @Test
    void createSession_ShouldThrowNotFoundException() {
        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Executable executable = () -> chatService.createSession("Session", userId.toString(), rateLimitBucket);
        assertThrows(NotFoundException.class, executable);

    }

    @Test
    void createSession_ShouldThrowDuplicateNameException() {
        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(sessionRepository.existsByNameAndUser("Session", user)).thenReturn(true);
        Executable executable = () -> chatService.createSession("Session", userId.toString(), rateLimitBucket);
        assertThrows(IllegalArgumentException.class, executable);

    }

    @Test
    void getAllSessions_ShouldReturnSessions() {
        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatSession> sessionPage = new PageImpl<>(java.util.List.of(session));
        when(sessionRepository.findByUserId(userId, pageable)).thenReturn(sessionPage);
        Page<ChatSessionResponse> result = chatService.getAllSessions(userId.toString(), pageable, rateLimitBucket);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllSessions_ShouldThrowRateLimitException() {
        when(rateLimitBucket.tryConsume(1)).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        Executable executable = () -> chatService.getAllSessions(userId.toString(), pageable, rateLimitBucket);
        assertThrows(RateLimitException.class, executable);

    }
}

