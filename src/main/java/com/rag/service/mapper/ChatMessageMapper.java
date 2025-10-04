package com.rag.service.mapper;

import com.rag.service.dto.AddMessageRequest;
import com.rag.service.dto.ChatMessageResponse;
import com.rag.service.dto.MessageResponseDto;
import com.rag.service.entity.ChatMessage;
import com.rag.service.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {UUID.class})
public interface ChatMessageMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "context", source = "retrievedContext")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
    MessageResponseDto toMessageResponseDto(ChatMessage message);

    List<MessageResponseDto> toMessageResponseDtoList(List<ChatMessage> messages);

    @Mapping(target = "sessionId", source = "session.id", qualifiedByName = "uuidToString")
    @Mapping(target = "sessionName", source = "session.name")
    @Mapping(target = "messages", source = "messages")
    ChatMessageResponse toChatMessageResponse(ChatSession session, List<MessageResponseDto> messages);

    @Mapping(target = "retrievedContext", source = "request.context")
    @Mapping(target = "session",source = "session")
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    ChatMessage toEntity(AddMessageRequest request, ChatSession session );

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_DATE_TIME) : null;
    }
}
