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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ApiKeyAuthFilterTest {
    private ApiKeyAuthFilter filter;
    private final String validApiKey = "test-api-key1,test-api-key2";

    @BeforeEach
    void setUp() {
        filter = new ApiKeyAuthFilter();
        try {

            var field = ApiKeyAuthFilter.class.getDeclaredField("apiKeys");
            field.setAccessible(true);
            field.set(filter, Set.of(validApiKey.split(",")));
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
        request.addHeader("X-API-Key", "test-api-key1");
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
        request.addHeader("X-API-Key", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void rateLimitExceeded_returns429() throws ServletException, IOException {
        FilterChain chain = mock(FilterChain.class);

        // Key1 should pass 100 requests
        for (int i = 0; i < 105; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-API-Key", "test-api-key1");
            request.setRequestURI("/protected/resource");

            filter.doFilter(request, response, chain);
            if (i < 100) {
                assertEquals(200, response.getStatus(), "Request " + i + " should pass");
            } else {
                assertEquals(429, response.getStatus(), "Request " + i + " should be rate limited");
            }

        }

    }

    @Test
    void multipleApiKeys_independentRateLimits() throws ServletException, IOException {
        FilterChain chain = mock(FilterChain.class);

        // Key1 should pass 100 requests
        for (int i = 0; i < 100; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-API-Key", "test-api-key1");
            request.setRequestURI("/protected/resource");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, chain);
            assertEquals(200, response.getStatus());
        }

        // Key2 should also pass 100 requests independently
        for (int i = 0; i < 100; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-API-Key", "test-api-key2");
            request.setRequestURI("/protected/resource");
            MockHttpServletResponse response = new MockHttpServletResponse();

            filter.doFilter(request, response, chain);
            assertEquals(200, response.getStatus());
        }
    }
}
