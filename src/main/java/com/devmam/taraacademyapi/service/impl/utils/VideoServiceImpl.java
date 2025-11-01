package com.devmam.taraacademyapi.service.impl.utils;

import com.devmam.taraacademyapi.constant.TableNames;
import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.entities.FileUpload;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.MinioService;
import com.devmam.taraacademyapi.service.VideoService;
import com.devmam.taraacademyapi.service.impl.entities.FileUploadService;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.UserCourseService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private FileUploadService fileUploadService;


    @Override
    public ResponseEntity<InputStreamResource> streamLessonVideoByLessonId(
            String authHeader,
            Integer id,
            String quality,
            String rangeHeader) throws Exception {

        Optional<Lesson> findingLesson = lessonService.getOne(id);

        if (findingLesson.isEmpty()) {
            throw new CommonException("Lesson not found");
        }
        Lesson lesson = findingLesson.get();

        if (lesson.getStatus() != 1 || lesson.getIsDeleted() == 1) {

            if (authHeader == null) {
                throw new AccessDeniedException("Access denied");
            }

            UUID userId = jwtService.getUserId(jwtService.getTokenFromAuthHeader(authHeader));

            Optional<UserCourse> findingUserCourse = userCourseService.findByUserIdAndCourseId(userId, lesson.getStage().getCourse().getId());
            if (findingUserCourse.isEmpty()) {
                throw new AccessDeniedException("Access denied");
            }
            if (findingUserCourse.get().getStatus() != 1 || findingUserCourse.get().getIsDeleted() == 1
                    || findingUserCourse.get().getExpiredAt().isBefore(Instant.now())) {
                throw new AccessDeniedException("Access denied");
            }

        }

        Optional<FileUpload> findingFileUpload = fileUploadService.findByFileRefAndReferenceId(TableNames.LESSON, lesson.getId());

        if (findingFileUpload.isEmpty()) {
            throw new FileNotFoundException("File not found");
        }

        String objectName = findingFileUpload.get().getFilePath();
        try {
            // Lấy metadata video để biết dung lượng
            StatObjectResponse stat = minioService.getMinioClient().statObject(
                    StatObjectArgs.builder()
                            .bucket(minioService.getBucketName())
                            .object(objectName)
                            .build()
            );
            long fileSize = stat.size();

            long rangeStart = 0;
            long rangeEnd = fileSize - 1;

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                rangeStart = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    rangeEnd = Long.parseLong(ranges[1]);
                }
                if (rangeEnd > fileSize - 1) {
                    rangeEnd = fileSize - 1;
                }
            }

            long contentLength = rangeEnd - rangeStart + 1;
            InputStream inputStream = minioService.getObjectRange(objectName, rangeStart, rangeEnd);

            return ResponseEntity.status(rangeHeader == null ? 200 : 206)
                    .header("Content-Type", stat.contentType())
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Length", String.valueOf(contentLength))
                    .header("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            throw new IOException("Error streaming video: " + e.getMessage(), e);
        }
    }
}
