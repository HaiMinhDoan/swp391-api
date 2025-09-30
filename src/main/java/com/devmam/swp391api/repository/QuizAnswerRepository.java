package com.devmam.swp391api.repository;

import com.devmam.swp391api.models.entities.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Integer>, JpaSpecificationExecutor<QuizAnswer> {
}