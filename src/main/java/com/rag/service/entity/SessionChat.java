package com.rag.service.entity;


import com.rag.service.dto.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "session_chat")
@Data
public class SessionChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "context")
    private String context;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conversation", columnDefinition = "jsonb")
    private List<Message> conversation;


}
