package config;

import com.rag.service.config.ApiKeyAuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ApiKeyAuthFilterTest {
    private ApiKeyAuthFilter filter;
    private final String validApiKey = "test-api-key";

    @BeforeEach
    void setUp() {
        filter = new ApiKeyAuthFilter();
        // Use reflection to set the private apiKey field
        try {
            var field = ApiKeyAuthFilter.class.getDeclaredField("apiKey");
            field.setAccessible(true);
            field.set(filter, validApiKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldAllowHealthEndpoint() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void doFilterInternal_ShouldAuthenticateWithValidApiKey() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure");
        request.addHeader("X-API-Key", validApiKey);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("API_USER", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_ShouldRejectWithInvalidApiKey() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure");
        request.addHeader("X-API-Key", "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldRejectWithMissingApiKey() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secure");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
