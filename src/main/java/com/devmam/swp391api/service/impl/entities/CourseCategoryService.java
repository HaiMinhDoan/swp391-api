package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.CourseCategory;
import com.devmam.taraacademyapi.repository.CourseCategoryRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseCategoryService extends BaseServiceImpl<CourseCategory, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public CourseCategoryService(CourseCategoryRepository repository) {
        super(repository);
    }
}
