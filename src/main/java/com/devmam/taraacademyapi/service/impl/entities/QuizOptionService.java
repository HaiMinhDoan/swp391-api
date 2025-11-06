package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.QuizOption;
import com.devmam.taraacademyapi.repository.QuizOptionRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizOptionService extends BaseServiceImpl<QuizOption, Integer> {

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private QuizOptionRepository quizOptionRepository;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public QuizOptionService(QuizOptionRepository repository) {
        super(repository);
    }
    
    @Transactional
    public void deleteAllByQuizId(Integer quizId) {
        quizOptionRepository.deleteAllByQuizId(quizId);
    }
    
    public long countByQuizId(Integer quizId) {
        return quizOptionRepository.findByQuizId(quizId).size();
    }
}
