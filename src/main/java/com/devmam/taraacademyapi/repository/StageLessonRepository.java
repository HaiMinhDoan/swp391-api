package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.StageLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageLessonRepository extends JpaRepository<StageLesson, Integer>, JpaSpecificationExecutor<StageLesson> {
    
    @Query("SELECT s FROM StageLesson s WHERE s.course.id = :courseId AND s.orderIndex = :orderIndex AND s.isDeleted = 0")
    Optional<StageLesson> findByCourseIdAndOrderIndex(@Param("courseId") Integer courseId, @Param("orderIndex") Integer orderIndex);
    
    @Query("SELECT s FROM StageLesson s WHERE s.course.id = :courseId AND s.orderIndex = :orderIndex AND s.id != :excludeId AND s.isDeleted = 0")
    Optional<StageLesson> findByCourseIdAndOrderIndexExcludingId(@Param("courseId") Integer courseId, @Param("orderIndex") Integer orderIndex, @Param("excludeId") Integer excludeId);
    
    @Query("SELECT s FROM StageLesson s WHERE s.course.id = :courseId AND s.isDeleted = 0 ORDER BY s.orderIndex ASC")
    List<StageLesson> findByCourseId(@Param("courseId") Integer courseId);
}