package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Ser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SerRepository extends JpaRepository<Ser, Integer>, JpaSpecificationExecutor<Ser> {
}