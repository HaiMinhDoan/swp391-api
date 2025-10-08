package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Career;
import com.devmam.taraacademyapi.repository.CareerRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CareerService extends BaseServiceImpl<Career, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public CareerService(CareerRepository repository) {
        super(repository);
    }
}