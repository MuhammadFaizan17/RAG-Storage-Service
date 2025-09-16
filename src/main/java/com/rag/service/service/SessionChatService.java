package com.rag.service.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.Message;
import com.rag.service.entity.SessionChat;
import com.rag.service.exception.BadRequestException;
import com.rag.service.repository.SessionChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionChatService {
    private final SessionChatRepository repository;
    private final ObjectMapper objectMapper;


    public List<Message> getPaginatedMessages(String sessionId, int page, int size) {
        int offset = page * size;
        int end = offset + size - 1;
        String jsonPath = String.format("$[%d to %d]", offset, end);

        String pagedJson = repository.getPaginatedMessages(sessionId, jsonPath);
        if (pagedJson == null) return new ArrayList<>();
        try {
            return Arrays.asList(objectMapper.readValue(pagedJson, Message[].class));
        } catch (Exception e) {
            throw new BadRequestException("Failed to parse paginated messages");
        }
    }

    @Transactional
    public void addMessage(String sessionId, AddMessageRequest message) throws JsonProcessingException {
        Optional<SessionChat> existing = repository.findBySessionId(sessionId);
        Message msg = Message.builder()
                .time(LocalDateTime.now().toString())
                .sender(message.getSender())
                .messageContent(message.getContent())
                .build();


        if (existing.isPresent()) {
            String newMessageJson = "[" + objectMapper.writeValueAsString(msg) + "]";
            repository.appendMessage(sessionId, newMessageJson);
        } else {
            SessionChat chat = new SessionChat();
            chat.setSessionId(sessionId);
            chat.setContext(message.getContext());
            List<Message> conversation = new ArrayList<>();
            conversation.add(msg);
            chat.setConversation(conversation);
            repository.save(chat);
        }
    }
}
