package com.devmam.taraacademyapi.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @Column(name = "username", length = 100)
    private String username;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 50)
    @Column(name = "customer_code", length = 50)
    private String customerCode;

    @ColumnDefault("0")
    @Column(name = "account_balance", precision = 15, scale = 2)
    private BigDecimal accountBalance;

    @Size(max = 250)
    @Column(name = "avt", length = 250)
    private String avt;

    @Size(max = 250)
    @Column(name = "role", length = 250)
    private String role;

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

    @Size(max = 100)
    @Column(name = "password", length = 100)
    private String password;

}