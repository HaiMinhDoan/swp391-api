package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Tran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TranRepository extends JpaRepository<Tran, Integer>, JpaSpecificationExecutor<Tran> {

    @Query("SELECT t FROM Tran t WHERE t.user.id = :userId")
    List<Tran> findByUserId(UUID userId);
}