package com.devmam.swp391api.repository;

import com.devmam.swp391api.models.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Integer>, JpaSpecificationExecutor<Certificate> {
}