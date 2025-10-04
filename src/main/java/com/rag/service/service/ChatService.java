package com.rag.service.service;

import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.ChatMessageResponse;
import com.rag.service.dto.ChatSessionResponse;
import com.rag.service.dto.CreateSessionRequest;
import com.rag.service.dto.PageableResponse;
import com.rag.service.entity.ChatMessage;
import com.rag.service.entity.ChatSession;
import com.rag.service.entity.User;
import com.rag.service.exception.NotFoundException;
import com.rag.service.exception.RateLimitException;
import com.rag.service.mapper.ChatMessageMapper;
import com.rag.service.mapper.ChatSessionMapper;
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
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    @Transactional
    public ChatSessionResponse createSession(CreateSessionRequest request, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);

        User user = userRepository.findById(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + request.getUserId()));

        if (sessionRepository.existsByNameAndUser(request.getName(), user)) {
            throw new IllegalArgumentException(SESSION_ALREADY_EXIST_MSG);
        }

        ChatSession session = chatSessionMapper.toEntity(request, user);

        session = sessionRepository.save(session);
        return chatSessionMapper.toChatSessionResponse(session);
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
        return chatSessionMapper.toChatSessionResponse(sessionRepository.save(session));
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

//        ChatMessage message = ChatMessage.builder()
//                .session(session)
//                .content(request.getContent())
//                .sender(request.getSender())
//                .retrievedContext(request.getContext())
//                .build();

        ChatMessage message=chatMessageMapper.toEntity(request,session);
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> getFavoriteSessions(String userId, Pageable pageable, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);
        return sessionRepository.findByUserIdAndFavoriteTrue(UUID.fromString(userId), pageable)
                .map(chatSessionMapper::toChatSessionResponse);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> getAllSessions(String userId, Pageable pageable, Bucket rateLimitBucket) {
        validateRateLimit(rateLimitBucket);
        return sessionRepository.findByUserId(UUID.fromString(userId), pageable)
                .map(chatSessionMapper::toChatSessionResponse);
    }

    @Transactional(readOnly = true)
    public PageableResponse getSessionMessages(String sessionId, Pageable pageable, Bucket bucket) {
        validateRateLimit(bucket);

        UUID sessionUuid = UUID.fromString(sessionId);
        ChatSession chatSession = sessionRepository.findById(sessionUuid)
                .orElseThrow(() -> new NotFoundException(SESSION_NOT_FOUND_MSG));

        Page<ChatMessage> sessionMessage = messageRepository.findBySessionIdOrderByCreatedAtDesc(sessionUuid, pageable);
        List<ChatMessage> messages = sessionMessage.getContent();

        ChatMessageResponse chatMessageResponse;
        if (messages.isEmpty()) {
            chatMessageResponse = ChatMessageResponse.builder()
                .sessionId(chatSession.getId().toString())
                .sessionName(chatSession.getName())
                .messages(new ArrayList<>())
                .build();
        } else {
            chatMessageResponse = chatMessageMapper.toChatMessageResponse(
                chatSession,
                chatMessageMapper.toMessageResponseDtoList(messages)
            );
        }

        return PageableResponse.builder()
                .data(chatMessageResponse)
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
