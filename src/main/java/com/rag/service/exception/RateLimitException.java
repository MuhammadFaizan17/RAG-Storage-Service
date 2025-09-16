package com.rag.service.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException() {
        super("Rate limit exceeded");
    }

    public RateLimitException(String message) {
        super(message);
    }
}
