package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.repository.QuizRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService extends BaseServiceImpl<Quiz, Integer> {

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private QuizOptionService quizOptionService;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public QuizService(QuizRepository repository) {
        super(repository);
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        // Delete all quiz options associated with this quiz first
        quizOptionService.deleteAllByQuizId(id);
        
        // Then delete the quiz
        super.delete(id);
    }
}
