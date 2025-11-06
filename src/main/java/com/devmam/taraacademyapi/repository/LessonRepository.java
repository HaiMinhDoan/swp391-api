package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer>, JpaSpecificationExecutor<Lesson> {
    
    @Query("SELECT l FROM Lesson l WHERE l.stage.id = :stageId AND l.orderIndex = :orderIndex AND l.isDeleted = 0")
    Optional<Lesson> findByStageIdAndOrderIndex(@Param("stageId") Integer stageId, @Param("orderIndex") Integer orderIndex);
    
    @Query("SELECT l FROM Lesson l WHERE l.stage.id = :stageId AND l.orderIndex = :orderIndex AND l.id != :excludeId AND l.isDeleted = 0")
    Optional<Lesson> findByStageIdAndOrderIndexExcludingId(@Param("stageId") Integer stageId, @Param("orderIndex") Integer orderIndex, @Param("excludeId") Integer excludeId);
}