package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>, JpaSpecificationExecutor<Message> {

    /**
     * Tìm tất cả messages của một chat, sắp xếp theo thời gian tạo tăng dần
     */
    List<Message> findByChatIdOrderByCreatedAtAsc(Integer chatId);

    /**
     * Tìm tất cả messages của một chat và có status cụ thể
     */
    List<Message> findByChatIdAndStatusOrderByCreatedAtAsc(Integer chatId, Integer status);

    /**
     * Đếm số lượng messages trong một chat
     */
    Long countByChatId(Integer chatId);
}