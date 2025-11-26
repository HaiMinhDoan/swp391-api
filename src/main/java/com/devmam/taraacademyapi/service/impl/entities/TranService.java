package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.repository.TranRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TranService extends BaseServiceImpl<Tran, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TranRepository tranRepository;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public TranService(TranRepository repository) {
        super(repository);
    }

    public List<Tran> getByUserId(UUID userId) {
        return tranRepository.findByUserId(userId);
    }

    public Optional<Tran> findById(Integer id) {
        return tranRepository.findById(id);
    }
}
