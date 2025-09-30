package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Certificate;
import com.devmam.taraacademyapi.repository.CertificateRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService extends BaseServiceImpl<Certificate, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public CertificateService(CertificateRepository repository) {
        super(repository);
    }
}
