package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Contact;
import com.devmam.taraacademyapi.repository.ContactRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService extends BaseServiceImpl<Contact, Integer> {
    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public ContactService(ContactRepository repository) {
        super(repository);
    }
}
