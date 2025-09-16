package controller;

import com.rag.service.controller.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HealthControllerTest {
    @Test
    void healthCheck_ShouldReturnStatusUpAndTimestamp() {
        HealthController controller = new HealthController();
        ResponseEntity<Map<String, String>> response = controller.healthCheck();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("UP", body.get("status"));
        assertNotNull(body.get("timestamp"));
        assertDoesNotThrow(() -> Long.parseLong(body.get("timestamp")));
    }
}

