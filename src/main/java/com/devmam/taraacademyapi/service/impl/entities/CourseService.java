package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Course;
import com.devmam.taraacademyapi.repository.CourseRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Course> findByStatus(Integer status) {
        return ((CourseRepository) repository).findByStatus(status);
    }

    public List<Course> findByCategoryId(Integer categoryId) {
        return ((CourseRepository) repository).findByCategoryId(categoryId);
    }

    public List<Course> findByTitleContaining(String title) {
        return ((CourseRepository) repository).findByTitleContaining(title);
    }

    public List<Course> findAllActive() {
        return ((CourseRepository) repository).findAllActive();
    }
}