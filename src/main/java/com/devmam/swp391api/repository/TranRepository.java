package com.devmam.swp391api.repository;

import com.devmam.swp391api.models.entities.Tran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TranRepository extends JpaRepository<Tran, Integer>, JpaSpecificationExecutor<Tran> {
}