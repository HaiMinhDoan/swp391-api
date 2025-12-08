package com.devmam.taraacademyapi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    // Nullable - chỉ set khi user đã đăng nhập
    @Column(name = "user_id")
    private UUID userId;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("1")
    @Column(name = "status")
    private Integer status;

    // Flag để đánh dấu chat anonymous
    @ColumnDefault("false")
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @OneToMany(mappedBy = "chat")
    private Set<Message> messages = new LinkedHashSet<>();
}