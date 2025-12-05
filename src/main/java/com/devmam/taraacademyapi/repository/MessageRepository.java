package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer>, JpaSpecificationExecutor<Message> {

    List<Message> findByChatIdOrderByCreatedAtAsc(Integer chatId);
}
