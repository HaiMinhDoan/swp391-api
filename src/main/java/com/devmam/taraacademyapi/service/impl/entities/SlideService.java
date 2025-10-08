package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Slide;
import com.devmam.taraacademyapi.repository.SlideRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlideService extends BaseServiceImpl<Slide, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public SlideService(SlideRepository repository) {
        super(repository);
    }
}