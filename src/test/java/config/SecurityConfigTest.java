package config;

import com.rag.service.config.ApiKeyAuthFilter;
import com.rag.service.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {
    @Test
    void securityFilterChain_ShouldCreateFilterChain() {
        ApiKeyAuthFilter filter = mock(ApiKeyAuthFilter.class);
        SecurityConfig config = new SecurityConfig(filter);
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        assertDoesNotThrow(() -> config.securityFilterChain(http));
    }
}

