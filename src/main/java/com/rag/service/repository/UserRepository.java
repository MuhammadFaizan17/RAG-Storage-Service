package com.rag.service.repository;

import com.rag.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Optional<User> findByName(String name);
}
