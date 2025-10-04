package com.rag.service.mapper;

import com.rag.service.dto.ChatSessionResponse;
import com.rag.service.dto.CreateSessionRequest;
import com.rag.service.dto.UpdateSessionRequest;
import com.rag.service.entity.ChatSession;
import com.rag.service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatSessionMapper {
    
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "userId", source = "user.id")
    ChatSessionResponse toChatSessionResponse(ChatSession chatSession);
    
    List<ChatSessionResponse> toChatSessionResponseList(List<ChatSession> chatSessions);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "name", source = "request.name")
    ChatSession toEntity(CreateSessionRequest request, User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "messages", ignore = true)
    void updateEntityFromDto(UpdateSessionRequest request, @MappingTarget ChatSession chatSession);
    
    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }
}
