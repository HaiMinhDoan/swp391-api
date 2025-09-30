package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.repository.StageLessonRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StageLessonService extends BaseServiceImpl<StageLesson, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public StageLessonService(StageLessonRepository repository) {
        super(repository);
    }
}
