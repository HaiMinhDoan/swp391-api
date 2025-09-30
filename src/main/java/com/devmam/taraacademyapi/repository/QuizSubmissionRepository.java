package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Integer>, JpaSpecificationExecutor<QuizSubmission> {
}