package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.EmailHistory;
import com.devmam.taraacademyapi.repository.EmailHistoryRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailHistoryService extends BaseServiceImpl<EmailHistory, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public EmailHistoryService(EmailHistoryRepository repository) {
        super(repository);
    }
}

