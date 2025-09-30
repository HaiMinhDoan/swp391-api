package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.TeacherCv;
import com.devmam.taraacademyapi.repository.TeacherCvRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherCvService extends BaseServiceImpl<TeacherCv, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public TeacherCvService(TeacherCvRepository repository) {
        super(repository);
    }
}
