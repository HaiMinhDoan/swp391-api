package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.QuizOption;
import com.devmam.taraacademyapi.repository.QuizOptionRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizOptionService extends BaseServiceImpl<QuizOption, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public QuizOptionService(QuizOptionRepository repository) {
        super(repository);
    }
}
