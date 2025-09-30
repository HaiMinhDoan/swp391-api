package com.devmam.swp391api.repository;

import com.devmam.swp391api.models.entities.Slide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Integer>, JpaSpecificationExecutor<Slide> {
}