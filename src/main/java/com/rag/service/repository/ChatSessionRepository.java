package com.rag.service.repository;

import com.rag.service.entity.ChatSession;
import com.rag.service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    Page<ChatSession> findByFavoriteTrue(Pageable pageable);
    Page<ChatSession> findByUserId(UUID userId, Pageable pageable);
    Page<ChatSession> findByUserIdAndFavoriteTrue(UUID userId, Pageable pageable);
    boolean existsByNameAndUser(String name, User user);

}
