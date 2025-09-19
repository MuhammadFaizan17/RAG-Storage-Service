package com.rag.service.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.rag.service.util.Constant.RATE_LIMIT_EXCEED_MSG;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.keys}")
    private String apiKeysConfig;

    private static final String API_KEY_HEADER = "X-API-Key";
    private Set<String> apiKeys;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (apiKeys == null) {
            apiKeys = Set.of(apiKeysConfig.split(","));
        }
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
        if (!apiKeys.contains(requestApiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }
        Bucket bucket = buckets.computeIfAbsent(requestApiKey, k -> Bucket4j.builder()
                .addLimit(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))))
                .build());
        if (!bucket.tryConsume(1)) {
            response.sendError(429, RATE_LIMIT_EXCEED_MSG);
            return;
        }
        var authentication = new UsernamePasswordAuthenticationToken(
            "API_USER", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
