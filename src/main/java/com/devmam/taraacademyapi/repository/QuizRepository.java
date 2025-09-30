package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer>, JpaSpecificationExecutor<Quiz> {
}