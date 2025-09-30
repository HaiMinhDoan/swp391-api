package com.devmam.swp391api.repository;

import com.devmam.swp391api.models.entities.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Integer>, JpaSpecificationExecutor<Career> {
}