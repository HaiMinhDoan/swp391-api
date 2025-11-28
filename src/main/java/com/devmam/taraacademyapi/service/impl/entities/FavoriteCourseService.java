package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.entities.FavoriteCourse;
import com.devmam.taraacademyapi.repository.FavoriteCourseRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FavoriteCourseService extends BaseServiceImpl<FavoriteCourse, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FavoriteCourseRepository favoriteCourseRepository;

    public FavoriteCourseService(FavoriteCourseRepository repository) {
        super(repository);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    @Transactional
    public FavoriteCourse create(FavoriteCourse entity) {
        // Validate unique constraint: user_id + course_id
        if (entity.getUser() != null && entity.getUser().getId() != null && entity.getCourse() != null && entity.getCourse().getId() != null) {
            Optional<FavoriteCourse> existing = favoriteCourseRepository.findByUserIdAndCourseId(
                    entity.getUser().getId(),
                    entity.getCourse().getId()
            );
            if (existing.isPresent()) {
                CommonException exception = new CommonException(
                        String.format("Favorite course với user_id=%s và course_id=%d đã tồn tại",
                                entity.getUser().getId(),
                                entity.getCourse().getId())
                );
                exception.setHttpStatus(HttpStatus.CONFLICT);
                exception.setData(null);
                throw exception;
            }
        }
        return super.create(entity);
    }
}
