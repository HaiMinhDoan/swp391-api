package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Integer>, JpaSpecificationExecutor<UserCourse> {
}