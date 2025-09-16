package service;

import com.rag.service.dto.UserResponse;
import com.rag.service.entity.User;
import com.rag.service.exception.NotFoundException;
import com.rag.service.repository.UserRepository;
import com.rag.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository userRepository;
    @InjectMocks UserService userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder().id(userId).name("Test User").email("test@example.com").build();
    }

    @Test
    void createUser_ShouldCreateUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse response = userService.createUser(user.getName(), user.getEmail());
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("Test User", response.getName());
    }

    @Test
    void createUser_ShouldThrowIfEmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        Executable executable = () -> userService.createUser(user.getName(), user.getEmail());
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void getUser_ShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserResponse response = userService.getUser(userId.toString());
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("Test User", response.getName());
    }

    @Test
    void getUser_ShouldThrowIfNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Executable executable = () -> userService.getUser(userId.toString());
        assertThrows(NotFoundException.class, executable);
    }
}

