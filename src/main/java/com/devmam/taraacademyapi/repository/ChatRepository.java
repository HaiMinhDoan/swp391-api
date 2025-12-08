package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {

    /**
     * Tìm tất cả chats có status cụ thể, sắp xếp theo updatedAt giảm dần
     */
    List<Chat> findByStatusOrderByUpdatedAtDesc(Integer status);

    /**
     * Tìm tất cả chats của một user
     */
    List<Chat> findByUserIdAndStatusOrderByUpdatedAtDesc(UUID userId, Integer status);

    /**
     * Tìm chat theo ID và userId (để verify ownership)
     */
    Optional<Chat> findByIdAndUserId(Integer id, UUID userId);

    /**
     * Tìm tất cả anonymous chats
     */
    List<Chat> findByIsAnonymousTrueAndStatusOrderByUpdatedAtDesc(Integer status);
}