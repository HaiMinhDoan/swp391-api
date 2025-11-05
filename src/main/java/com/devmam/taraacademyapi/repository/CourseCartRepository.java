package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.CourseCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCartRepository extends JpaRepository<CourseCart, Integer>, JpaSpecificationExecutor<CourseCart> {
}