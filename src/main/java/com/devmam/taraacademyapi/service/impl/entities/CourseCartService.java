package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.CourseCart;
import com.devmam.taraacademyapi.repository.CourseCartRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseCartService extends BaseServiceImpl<CourseCart, Integer> {

    @Autowired
    private EntityManager entityManager;

    public CourseCartService(CourseCartRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
