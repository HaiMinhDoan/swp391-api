package com.devmam.taraacademyapi.service.impl.entities;

import com.devmam.taraacademyapi.constant.TableNames;
import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.dto.request.FileUploadMultipartRequestDto;
import com.devmam.taraacademyapi.models.dto.request.LessonCreatingDto;
import com.devmam.taraacademyapi.models.dto.response.FileUploadResultDto;
import com.devmam.taraacademyapi.models.dto.response.LessonResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.StageLesson;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.repository.LessonRepository;
import com.devmam.taraacademyapi.service.FileOperationsService;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.BaseServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class LessonService extends BaseServiceImpl<Lesson, Integer> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private StageLessonService stageLessonService;

    @Autowired
    private FileOperationsService fileOperationsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private LessonRepository lessonRepository;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public LessonService(LessonRepository repository) {
        super(repository);
    }
    
    @Override
    @Transactional
    public Lesson create(Lesson entity) {
        // Validate unique constraint: stage_id + order_index
        if (entity.getStage() != null && entity.getStage().getId() != null && entity.getOrderIndex() != null) {
            Optional<Lesson> existing = lessonRepository.findByStageIdAndOrderIndex(
                    entity.getStage().getId(), 
                    entity.getOrderIndex()
            );
            if (existing.isPresent()) {
                CommonException exception = new CommonException(
                        String.format("Lesson với stage_id=%d và order_index=%d đã tồn tại", 
                                entity.getStage().getId(), 
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
    public Lesson update(Integer id, Lesson entity) {
        // Validate unique constraint: stage_id + order_index (excluding current id)
        if (entity.getStage() != null && entity.getStage().getId() != null && entity.getOrderIndex() != null) {
            Optional<Lesson> existing = lessonRepository.findByStageIdAndOrderIndexExcludingId(
                    entity.getStage().getId(), 
                    entity.getOrderIndex(),
                    id
            );
            if (existing.isPresent()) {
                CommonException exception = new CommonException(
                        String.format("Lesson với stage_id=%d và order_index=%d đã tồn tại", 
                                entity.getStage().getId(), 
                                entity.getOrderIndex())
                );
                exception.setHttpStatus(HttpStatus.CONFLICT);
                exception.setData(null);
                throw exception;
            }
        }
        return super.update(id, entity);
    }

    @Transactional
    public ResponseEntity<ResponseData<LessonResponseDto>> createLessonWithVideo(String authHeader, LessonCreatingDto requestDto, MultipartFile file){

        if (authHeader == null) {
            throw new AccessDeniedException("Access denied");
        }
        if(file.getContentType() != null && !file.getContentType().equals("video/mp4")){
            throw new CommonException("File type must be video/mp4");
        }

        UUID userId = jwtService.getUserId(jwtService.getTokenFromAuthHeader(authHeader));
        Optional<User> findingUser = userService.getOne(userId);
        if (findingUser.isEmpty()) {
            throw new AccessDeniedException("Access denied");
        }

        // Get stage entity
        StageLesson stage = null;
        if (requestDto.getStageId() != null) {
            stage = stageLessonService.getOne(requestDto.getStageId()).orElse(null);
        }

        Lesson lesson = new Lesson();
        lesson.setStage(stage);
        lesson.setTitle(requestDto.getTitle());
        lesson.setContent(requestDto.getContent());
        lesson.setOrderIndex(requestDto.getOrderIndex() != null ? requestDto.getOrderIndex() : 0);
        lesson.setCreatedBy(findingUser.get());
        lesson.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        lesson.setIsDeleted(0);
        lesson.setCreatedAt(Instant.now());
        lesson.setUpdatedAt(Instant.now());

        lesson = this.create(lesson);

        if (file != null) {
            FileUploadMultipartRequestDto fileUploadRequestDto = FileUploadMultipartRequestDto.builder()
                    .file(file)
                    .fileType("video/mp4")
                    .fileRef(TableNames.LESSON)
                    .referenceId(lesson.getId())
                    .description(lesson.getContent() + " video")
                    .status(lesson.getStatus())
                    .customFileName(lesson.getTitle() + ".mp4")
                    .build();
            FileUploadResultDto result = fileOperationsService.uploadFile(fileUploadRequestDto);
            lesson.setVideoUrl(result.getPublicUrl());
        }
        lesson = this.update(lesson.getId(), lesson);
        return ResponseEntity.ok(
                ResponseData.<LessonResponseDto>builder()
                        .status(200)
                        .message("Lesson created successfully")
                        .error(null)
                        .data(LessonResponseDto.toDTO(lesson))
                        .build()
        );
    }

}
