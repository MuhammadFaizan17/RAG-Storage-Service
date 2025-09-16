package com.rag.service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    @Value("${api.key:default-api-key}")
    private String apiKey;

    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (uri.equals("/api/health") ||
                uri.equals("/swagger-ui.html") ||
                uri.startsWith("/swagger-ui/") ||
                uri.startsWith("/api-docs") ||
                uri.equals("/actuator/health") ||
                uri.startsWith("/actuator/") ||
                uri.equals("/actuator/prometheus") ||
                uri.equals("/v3/api-docs") ||
                uri.startsWith("/v3/api-docs") ||
                uri.equals("/ragchat/api/health") ||
                uri.equals("/ragchat/api/metrics") ||
                uri.equals("/ragchat/api/info") ||
                uri.equals("/ragchat/api/prometheus") ||
                uri.equals("/ragchat/swagger-ui.html") ||
                uri.startsWith("/ragchat/swagger-ui/") ||
                uri.startsWith("/ragchat/api-docs") ||
                uri.startsWith("/ragchat/actuator/") ||
                uri.equals("/ragchat/actuator/prometheus") ||
                uri.equals("/ragchat/v3/api-docs") ||
                uri.startsWith("/ragchat/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey.equals(requestApiKey)) {
            var authentication = new UsernamePasswordAuthenticationToken(
                "API_USER", null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
