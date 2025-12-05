package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.EmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, Integer>, JpaSpecificationExecutor<EmailHistory> {
}

