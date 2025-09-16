package com.rag.service.repository;


import com.rag.service.entity.SessionChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessionChatRepository extends JpaRepository<SessionChat, Long> {
    Optional<SessionChat> findBySessionId(String sessionId);

    @Query(
            value = "SELECT jsonb_path_query_array(conversation, cast(:jsonPath as jsonpath)) FROM session_chat WHERE session_id = :sessionId",
            nativeQuery = true
    )
    String getPaginatedMessages(@Param("sessionId") String sessionId, @Param("jsonPath") String jsonPath);

    @Modifying
    @Query(
            value = "UPDATE session_chat SET conversation = conversation || cast(:newMessage as jsonb) WHERE session_id = :sessionId",
            nativeQuery = true
    )
    void appendMessage(@Param("sessionId") String sessionId, @Param("newMessage") String newMessageJson);
}
