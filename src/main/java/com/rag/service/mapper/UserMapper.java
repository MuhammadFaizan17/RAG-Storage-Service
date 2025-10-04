package com.rag.service.mapper;

import com.rag.service.dto.CreateUserRequest;
import com.rag.service.dto.UserResponse;
import com.rag.service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(CreateUserRequest createUserRequest);
}
