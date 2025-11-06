package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Integer>, JpaSpecificationExecutor<QuizOption> {
    
    @Query("SELECT qo FROM QuizOption qo WHERE qo.quiz.id = :quizId")
    List<QuizOption> findByQuizId(@Param("quizId") Integer quizId);
    
    @Modifying
    @Query("DELETE FROM QuizOption qo WHERE qo.quiz.id = :quizId")
    void deleteAllByQuizId(@Param("quizId") Integer quizId);
}