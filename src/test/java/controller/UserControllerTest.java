package controller;

import com.rag.service.controller.UserController;
import com.rag.service.dto.CreateUserRequest;
import com.rag.service.dto.UserResponse;
import com.rag.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private CreateUserRequest createUserRequest;
    private UserResponse userResponse;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test User");
        createUserRequest.setEmail("test@example.com");
        userResponse = UserResponse.builder()
                .id(UUID.fromString(userId))
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        when(userService.createUser(createUserRequest.getName(), createUserRequest.getEmail()))
                .thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).createUser(createUserRequest.getName(), createUserRequest.getEmail());
    }

    @Test
    void getUser_ShouldReturnUserById() {
        when(userService.getUser(userId)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
        verify(userService).getUser(userId);
    }
}
