package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.FavoriteCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteCourseRepository extends JpaRepository<FavoriteCourse, Integer>, JpaSpecificationExecutor<FavoriteCourse> {
    Optional<FavoriteCourse> findByUserIdAndCourseId(UUID userId, Integer courseId);
}
