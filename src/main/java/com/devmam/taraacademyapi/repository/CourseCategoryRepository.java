package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer>, JpaSpecificationExecutor<CourseCategory> {
}