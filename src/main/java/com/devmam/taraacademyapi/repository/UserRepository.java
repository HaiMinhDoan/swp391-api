package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findByOtpAndStatus(String otp, Integer status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isDeleted = 0")
    Long countActiveUsers();
}