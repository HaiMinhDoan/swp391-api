package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.repository.StageLessonRepository;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StageLessonService extends BaseServiceImpl<StageLesson, Integer> {

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private StageLessonRepository stageLessonRepository;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public StageLessonService(StageLessonRepository repository) {
        super(repository);
    }
    
    @Override
    @Transactional
    public StageLesson create(StageLesson entity) {
        // Validate unique constraint: course_id + order_index
        if (entity.getCourse() != null && entity.getCourse().getId() != null && entity.getOrderIndex() != null) {
            Optional<StageLesson> existing = stageLessonRepository.findByCourseIdAndOrderIndex(
                    entity.getCourse().getId(), 
                    entity.getOrderIndex()
            );
            if (existing.isPresent()) {
                CommonException exception = new CommonException(
                        String.format("Stage lesson với course_id=%d và order_index=%d đã tồn tại", 
                                entity.getCourse().getId(), 
                                entity.getOrderIndex())
                );
                exception.setHttpStatus(HttpStatus.CONFLICT);
                exception.setData(null);
                throw exception;
            }
        }
        return super.create(entity);
    }
    
    @Override
    @Transactional
    public StageLesson update(Integer id, StageLesson entity) {
        // Validate unique constraint: course_id + order_index (excluding current id)
        if (entity.getCourse() != null && entity.getCourse().getId() != null && entity.getOrderIndex() != null) {
            Optional<StageLesson> existing = stageLessonRepository.findByCourseIdAndOrderIndexExcludingId(
                    entity.getCourse().getId(), 
                    entity.getOrderIndex(),
                    id
            );
            if (existing.isPresent()) {
                CommonException exception = new CommonException(
                        String.format("Stage lesson với course_id=%d và order_index=%d đã tồn tại", 
                                entity.getCourse().getId(), 
                                entity.getOrderIndex())
                );
                exception.setHttpStatus(HttpStatus.CONFLICT);
                exception.setData(null);
                throw exception;
            }
        }
        return super.update(id, entity);
    }
}
