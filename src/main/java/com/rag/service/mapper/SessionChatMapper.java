package com.rag.service.mapper;

import com.rag.service.dto.Message;
import com.rag.service.entity.SessionChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SessionChatMapper {

    @Mapping(target = "messageContent", source = "content")
    Message toMessage(String content, String sender, String time);


    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "context", source = "context")
    @Mapping(target = "conversation", source = "messages")
    SessionChat toSessionChat(String sessionId, String context, List<Message> messages);
}
