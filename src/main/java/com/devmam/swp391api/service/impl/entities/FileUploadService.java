package com.devmam.swp391api.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.FileUpload;
import com.devmam.taraacademyapi.repository.FileUploadRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileUploadService extends BaseServiceImpl<FileUpload, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public FileUploadService(FileUploadRepository repository) {
        super(repository);
    }
}
