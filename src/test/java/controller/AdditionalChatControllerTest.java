package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rag.service.controller.AdditionalChatController;
import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.Message;
import com.rag.service.service.SessionChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdditionalChatControllerTest {
    @Mock
    private SessionChatService service;

    @InjectMocks
    private AdditionalChatController controller;

    private String sessionId;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setUp() {
        sessionId = "test-session-id";
        message1 = Message.builder()
                .sender("user1")
                .messageContent("Hello")
                .time(LocalDateTime.now().toString())
                .build();
        message2 = Message.builder()
                .sender("user2")
                .messageContent("World")
                .time(LocalDateTime.now().toString())
                .build();
    }

    @Test
    void getMessages_ShouldReturnPaginatedMessages() {
        List<Message> messages = Arrays.asList(message1, message2);
        when(service.getPaginatedMessages(sessionId, 0, 10)).thenReturn(messages);

        ResponseEntity<List<Message>> response = controller.getMessages(sessionId, 0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages, response.getBody());
        verify(service).getPaginatedMessages(sessionId, 0, 10);
    }

    @Test
    void addMessage_ShouldCallServiceAndReturnOk() throws JsonProcessingException {
        AddMessageRequest req = new AddMessageRequest();
        req.setContent("Test message");
        doNothing().when(service).addMessage(eq(sessionId), any(AddMessageRequest.class));

        ResponseEntity<Void> response = controller.addMessage(sessionId, req);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service).addMessage(eq(sessionId), any(AddMessageRequest.class));
    }
}
