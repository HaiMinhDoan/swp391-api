package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer>, JpaSpecificationExecutor<Quiz> {
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.lesson.id = :lessonId AND q.isDeleted = 0")
    Long countByLessonId(@Param("lessonId") Integer lessonId);
}