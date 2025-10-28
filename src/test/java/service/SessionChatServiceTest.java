package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.Message;
import com.rag.service.entity.SessionChat;
import com.rag.service.mapper.SessionChatMapper;
import com.rag.service.repository.SessionChatRepository;
import com.rag.service.service.SessionChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionChatServiceTest {
    @Mock SessionChatRepository repository;
    @Mock ObjectMapper objectMapper;
    @Mock SessionChatMapper sessionChatMapper;
    @InjectMocks SessionChatService service;

    private String sessionId;
    private AddMessageRequest addMessageRequest;
    private Message message;
    private SessionChat sessionChat;

    @BeforeEach
    void setUp() {
        sessionId = "session-1";
        addMessageRequest = new AddMessageRequest();
        addMessageRequest.setSender("user1");
        addMessageRequest.setContent("Hello");
        message = Message.builder().sender("user1").messageContent("Hello").time("2025-09-16T10:00:00").build();
        sessionChat = new SessionChat();
        sessionChat.setSessionId(sessionId);
        sessionChat.setConversation(java.util.Collections.emptyList());
    }

    @Test
    void getPaginatedMessages_ShouldReturnMessages() throws Exception {
        String json = "[{'sender':'user1','message':'Hello','time':'2025-09-16T10:00:00'}]";
        when(repository.getPaginatedMessages(sessionId, "$[0 to 9]"))
                .thenReturn(json);
        when(objectMapper.readValue(json, Message[].class))
                .thenReturn(new Message[]{message});
        List<Message> result = service.getPaginatedMessages(sessionId, 0, 10);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).getMessageContent());
    }

    @Test
    void getPaginatedMessages_ShouldReturnEmptyListIfNull() {
        when(repository.getPaginatedMessages(sessionId, "$[0 to 9]"))
                .thenReturn(null);
        List<Message> result = service.getPaginatedMessages(sessionId, 0, 10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPaginatedMessages_ShouldThrowOnJsonParseError() throws Exception {
        String json = "invalid json";
        when(repository.getPaginatedMessages(sessionId, "$[0 to 9]"))
                .thenReturn(json);
        when(objectMapper.readValue(json, Message[].class))
                .thenThrow(new JsonProcessingException("Parse error") {});
        assertThrows(RuntimeException.class, () -> service.getPaginatedMessages(sessionId, 0, 10));
    }

    @Test
    void addMessage_ShouldUpdateExistingSession() throws Exception {
        when(repository.findBySessionId(sessionId)).thenReturn(Optional.of(sessionChat));
        doReturn("[]").when(objectMapper).writeValueAsString(any());
        service.addMessage(sessionId, addMessageRequest);
        verify(repository).appendMessage(eq(sessionId), anyString());
    }


    @Test
    void addMessage_ShouldThrowJsonProcessingException() throws Exception {
        when(repository.findBySessionId(sessionId)).thenReturn(Optional.of(sessionChat));
        doThrow(new JsonProcessingException("error") {}).when(objectMapper).writeValueAsString(any());
        assertThrows(JsonProcessingException.class, () -> service.addMessage(sessionId, addMessageRequest));
    }
}
