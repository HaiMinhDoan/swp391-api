package com.devmam.taraacademyapi.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Tran {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Size(max = 50)
    @Column(name = "method", length = 50)
    private String method;

    @Column(name = "detail")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> detail;

    @Column(name = "response_code")
    private Integer responseCode;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("1")
    @Column(name = "status")
    private Integer status;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Integer isDeleted;

    // map voi user course
    @OneToMany(mappedBy = "tran", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCourse> userCourses;

}