package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Integer>, JpaSpecificationExecutor<QuizOption> {
}