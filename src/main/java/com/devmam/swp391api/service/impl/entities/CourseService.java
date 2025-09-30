package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.repository.CourseRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService extends BaseServiceImpl<Course, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public CourseService(CourseRepository repository) {
        super(repository);
    }
}
