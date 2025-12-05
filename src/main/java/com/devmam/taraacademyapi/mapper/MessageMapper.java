package com.devmam.taraacademyapi.mapper;

import com.devmam.taraacademyapi.models.dto.response.MessageDto;
import com.devmam.taraacademyapi.models.entities.Message;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDto toDto(Message message);
    List<MessageDto> toDtoList(List<Message> messages);
    default Page<MessageDto> toDtoPage(Page<Message> messages){
        if(messages == null) return Page.empty();
        return messages.map(this::toDto);
    }
}
