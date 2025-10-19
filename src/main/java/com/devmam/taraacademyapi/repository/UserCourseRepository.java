package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Integer>, JpaSpecificationExecutor<UserCourse> {

    @Query("SELECT uc FROM UserCourse uc WHERE uc.user.id = :userId AND uc.course.id = :courseId")
    Optional<UserCourse> findByUserIdAndCourseId(UUID userId, Integer courseId);

    List<UserCourse> findByUserId(UUID userId);
}