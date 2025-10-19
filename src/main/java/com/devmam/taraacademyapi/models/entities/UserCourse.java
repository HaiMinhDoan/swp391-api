package com.devmam.taraacademyapi.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_courses", indexes = {
        @Index(name = "idx_user_course_unique", columnList = "user_id, course_id", unique = true)
})
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_course_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Tran tran;

    @ColumnDefault("now()")
    @Column(name = "enrolled_at")
    private Instant enrolledAt;

    @NotNull
    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @ColumnDefault("0")
    @Column(name = "progress", precision = 5, scale = 2)
    private BigDecimal progress;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("1")
    @Column(name = "current_lesson_id")
    private Integer currentLessonId;

    @ColumnDefault("1")
    @Column(name = "current_stage_id")
    private Integer currentStageId;

    @ColumnDefault("1")
    @Column(name = "status")
    private Integer status;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Integer isDeleted;
}