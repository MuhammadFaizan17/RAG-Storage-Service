package com.rag.service.service;

import com.rag.service.dto.CreateUserRequest;
import com.rag.service.dto.UserResponse;
import com.rag.service.entity.User;
import com.rag.service.exception.NotFoundException;
import com.rag.service.mapper.UserMapper;
import com.rag.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.rag.service.util.Constant.EMAIL_ALREADY_EXIST;
import static com.rag.service.util.Constant.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(String name, String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXIST);
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXIST);
        }

        User user = userMapper.toUser(request);
        user = userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + id));
        return userMapper.toUserResponse(user);
    }
}
