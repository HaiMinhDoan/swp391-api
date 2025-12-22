package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {

    @Query("SELECT c FROM Course c WHERE c.isDeleted = 0")
    List<Course> findAllActive();

    @Query("SELECT c FROM Course c WHERE c.category.status != 0 and c.isDeleted = 0 and c.status != 0")
    List<Course> findAllCateActiveAndCourseActive();

    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.isDeleted = 0")
    List<Course> findByStatus(@Param("status") Integer status);

    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId AND c.isDeleted = 0")
    List<Course> findByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("SELECT c FROM Course c WHERE c.title LIKE %:title% AND c.isDeleted = 0")
    List<Course> findByTitleContaining(@Param("title") String title);
}