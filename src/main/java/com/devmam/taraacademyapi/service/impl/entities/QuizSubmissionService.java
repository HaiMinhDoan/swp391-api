package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.QuizSubmission;
import com.devmam.taraacademyapi.repository.QuizSubmissionRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizSubmissionService extends BaseServiceImpl<QuizSubmission, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public QuizSubmissionService(QuizSubmissionRepository repository) {
        super(repository);
    }
}
