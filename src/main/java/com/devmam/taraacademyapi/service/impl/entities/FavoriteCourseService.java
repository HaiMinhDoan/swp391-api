package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.FavoriteCourse;
import com.devmam.taraacademyapi.repository.FavoriteCourseRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteCourseService extends BaseServiceImpl<FavoriteCourse, Integer> {

    @Autowired
    private EntityManager entityManager;

    public FavoriteCourseService(FavoriteCourseRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
