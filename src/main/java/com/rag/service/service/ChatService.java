package com.rag.service.service;

import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.ChatMessageResponse;
import com.rag.service.dto.ChatSessionResponse;
import com.rag.service.dto.MessageResponseDto;
import com.rag.service.dto.PageableResponse;
import com.rag.service.entity.ChatMessage;
import com.rag.service.entity.ChatSession;
import com.rag.service.entity.User;
import com.rag.service.exception.NotFoundException;
import com.rag.service.exception.RateLimitException;
import com.rag.service.repository.ChatMessageRepository;
import com.rag.service.repository.ChatSessionRepository;
import com.rag.service.repository.UserRepository;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.rag.service.util.Constant.RATE_LIMIT_EXCEED_MSG;
import static com.rag.service.util.Constant.SESSION_ALREADY_EXIST_MSG;
import static com.rag.service.util.Constant.SESSION_NOT_FOUND_MSG;
import static com.rag.service.util.Constant.UNAUTHORISED_SESSION_USER_MSG;
import static com.rag.service.util.Constant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    private ChatSessionResponse toSessionResponse(ChatSession session) {
        return ChatSessionResponse.builder()
                .id(session.getId())
                .name(session.getName())
                .favorite(session.isFavorite())
                .createdAt(session.getCreatedAt() == null ? null : session.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                .updatedAt(session.getUpdatedAt() == null ? null : session.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                .userId(session.getUser().getId())
                .build();
    }

    private ChatMessageResponse toMessageResponse(List<ChatMessage> message, String sessionId, String sessionName) {
        if (null == message || message.isEmpty()) {
            return ChatMessageResponse.builder().messages(new ArrayList<>()).sessionName(sessionName).sessionId(sessionId).build();
        }
        return ChatMessageResponse.builder().sessionName(sessionName)
                .sessionId(sessionId)

                .messages(message.stream().map(m -> MessageResponseDto.builder()
                        .id(m.getId().toString())
                        .content(m.getContent())
                        .sender(m.getSender())
                        .createdAt(m.getCreatedAt().toString())
                        .context(m.getRetrievedContext())
                        .build()).toList())
                .build();
    }

    @Transactional
    public ChatSessionResponse createSession(String name, String userId, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + userId));

        if (sessionRepository.existsByNameAndUser(name, user)) {
            throw new IllegalArgumentException(SESSION_ALREADY_EXIST_MSG);
        }

        ChatSession session = ChatSession.builder()
                .name(name)
                .user(user)
                .favorite(false)
                .build();
        session = sessionRepository.save(session);
        return toSessionResponse(session);
    }

    @Transactional
    public ChatSessionResponse updateSession(String sessionId, String name, Boolean favorite, String userId, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);

        UUID userUUID = UUID.fromString(userId);
        UUID sessionUUID = UUID.fromString(sessionId);

        ChatSession session = sessionRepository.findById(sessionUUID)
                .filter(s -> s.getUser().getId().equals(userUUID))
                .orElseThrow(() -> new NotFoundException(SESSION_NOT_FOUND_MSG));

        if (name != null && !name.equals(session.getName())) {
            if (sessionRepository.existsByNameAndUser(name, session.getUser())) {
                throw new IllegalArgumentException(SESSION_ALREADY_EXIST_MSG);
            }
            session.setName(name);
        }

        if (favorite != null) {
            session.setFavorite(favorite);
        }
        return toSessionResponse(sessionRepository.save(session));
    }

    @Transactional
    public void saveMessage(String sessionId, AddMessageRequest request, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);

        UUID sessionUUID = UUID.fromString(sessionId);
        UUID userUUID = UUID.fromString(request.getUserId());

        ChatSession session = sessionRepository.findById(sessionUUID)
                .orElseThrow(() -> new NotFoundException(SESSION_NOT_FOUND_MSG));

        if (!session.getUser().getId().equals(userUUID)) {
            throw new IllegalArgumentException(UNAUTHORISED_SESSION_USER_MSG);
        }

        ChatMessage message = ChatMessage.builder()
                .session(session)
                .content(request.getContent())
                .sender(request.getSender())
                .retrievedContext(request.getContext())
                .build();
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> getFavoriteSessions(String userId, Pageable pageable, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);
        return sessionRepository.findByUserIdAndFavoriteTrue(UUID.fromString(userId), pageable)
                .map(this::toSessionResponse);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> getAllSessions(String userId, Pageable pageable, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);
        return sessionRepository.findByUserId(UUID.fromString(userId), pageable)
                .map(this::toSessionResponse);
    }

    @Transactional(readOnly = true)
    public PageableResponse getSessionMessages(String sessionId, Pageable pageable, Bucket bucket) {
        validateRateLimit(bucket);

        UUID sessionUuid = UUID.fromString(sessionId);
        ChatSession chatSession = sessionRepository.findById(sessionUuid)
                .orElseThrow(() -> new NotFoundException(SESSION_NOT_FOUND_MSG));

        Page<ChatMessage> sessionMessage = messageRepository.findBySessionIdOrderByCreatedAtDesc(sessionUuid, pageable);
        return PageableResponse.builder()
                .data(toMessageResponse(sessionMessage.getContent(), chatSession.getId().toString(), chatSession.getName()))
                .totalPages(sessionMessage.getTotalPages())
                .totalElements(sessionMessage.getTotalElements())
                .build();
    }

    @Transactional
    public void deleteSession(String sessionId, String userId, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);
        UUID userUUID = UUID.fromString(userId);
        UUID sessionUUID = UUID.fromString(sessionId);

        ChatSession session = sessionRepository.findById(sessionUUID)
                .filter(s -> s.getUser().getId().equals(userUUID))
                .orElseThrow(() -> new NotFoundException(SESSION_NOT_FOUND_MSG));
        sessionRepository.delete(session);
    }

    private static void validateRateLimit(Bucket rateLimitBucket) {
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitException(RATE_LIMIT_EXCEED_MSG);
        }
    }
}
