package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Integer>, JpaSpecificationExecutor<Career> {
}