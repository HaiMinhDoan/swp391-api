package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Integer>, JpaSpecificationExecutor<QuizAnswer> {
}