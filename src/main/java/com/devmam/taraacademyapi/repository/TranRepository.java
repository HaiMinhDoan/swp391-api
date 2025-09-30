package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Tran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TranRepository extends JpaRepository<Tran, Integer>, JpaSpecificationExecutor<Tran> {
}