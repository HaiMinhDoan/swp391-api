package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Ser;
import com.devmam.taraacademyapi.repository.SerRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SerService extends BaseServiceImpl<Ser, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public SerService(SerRepository repository) {
        super(repository);
    }
}
