package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.repository.UserCourseRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserCourseService extends BaseServiceImpl<UserCourse, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserCourseRepository userCourseRepository;

    public UserCourseService(UserCourseRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public Optional<UserCourse> findByUserIdAndCourseId(UUID userId, Integer courseId) {
        return userCourseRepository.findByUserIdAndCourseId(userId, courseId);
    }
}
