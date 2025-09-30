package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.QuizAnswer;
import com.devmam.taraacademyapi.repository.QuizAnswerRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerService extends BaseServiceImpl<QuizAnswer, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public QuizAnswerService(QuizAnswerRepository repository) {
        super(repository);
    }
}
