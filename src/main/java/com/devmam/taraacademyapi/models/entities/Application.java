package com.devmam.taraacademyapi.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @Column(name = "cv_url", length = Integer.MAX_VALUE)
    private String cvUrl;

    @ColumnDefault("1")
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "interview_date")
    private Instant interviewDate;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "final_note", length = Integer.MAX_VALUE)
    private String finalNote;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "phone")
    private String phone;

    @Column(name = "gender")
    private Short gender;

    @Column(name = "oboard_date")
    private Instant oboardDate;

    @Size(max = 255)
    @Column(name = "interview_type")
    private String interviewType;

    @Size(max = 255)
    @Column(name = "meeting_link")
    private String meetingLink;
}

