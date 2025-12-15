package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.QuizRequestDto;
import com.devmam.taraacademyapi.models.dto.response.QuizExcelImportResultDto;
import com.devmam.taraacademyapi.models.dto.response.QuizResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.service.QuizExcelImportService;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import com.devmam.taraacademyapi.service.impl.utils.QuizExcelTemplateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/quizzes")
@PreAuthorize("permitAll()")
public class QuizController extends BaseController<Quiz, Integer, QuizRequestDto, QuizResponseDto> {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private LessonService lessonService;

    @Autowired
    private QuizExcelImportService quizExcelImportService;

    @Autowired
    private QuizExcelTemplateGenerator quizExcelTemplateGenerator;

    public QuizController(QuizService quizService) {
        super(quizService);
    }

    @Override
    protected QuizResponseDto toResponseDto(Quiz quiz) {
        return QuizResponseDto.toDTO(quiz);
    }

    @Override
    protected Quiz toEntity(QuizRequestDto requestDto) {
        // Get lesson entity
        Lesson lesson = null;
        if (requestDto.getLessonId() != null) {
            lesson = lessonService.getOne(requestDto.getLessonId()).orElse(null);
        }

        Quiz quiz = new Quiz();
        quiz.setLesson(lesson);
        quiz.setType(requestDto.getType());
        quiz.setQuestion(requestDto.getQuestion());
        quiz.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : 1);
        quiz.setIsDeleted(0);
        quiz.setFileUrl(requestDto.getFileUrl());
        quiz.setTeacherNote(requestDto.getTeacherNote());
        quiz.setCreatedAt(Instant.now());
        quiz.setUpdatedAt(Instant.now());

        return quiz;
    }

    @Override
    protected Page<QuizResponseDto> convertPage(Page<Quiz> quizPage) {
        return QuizResponseDto.convertPage(quizPage);
    }

    @Override
    protected String getEntityName() {
        return "Quiz";
    }

    /**
     * Import quizzes from Excel file
     * Excel format: Lesson ID | Type | Question | Answer | Status | Teacher Note | Option 1 | Is Correct 1 | Option 2 | Is Correct 2 | Option 3 | Is Correct 3 | Option 4 | Is Correct 4
     */
    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ResponseData<QuizExcelImportResultDto>> importQuizzesFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<QuizExcelImportResultDto>builder()
                                .status(400)
                                .message("File is required")
                                .error("File cannot be empty")
                                .data(null)
                                .build());
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                 !contentType.equals("application/vnd.ms-excel"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseData.<QuizExcelImportResultDto>builder()
                                .status(400)
                                .message("Invalid file type")
                                .error("File must be an Excel file (.xlsx or .xls)")
                                .data(null)
                                .build());
            }

            // Import quizzes
            QuizExcelImportResultDto result = quizExcelImportService.importQuizzesFromExcel(file);

            String message = String.format("Import completed: %d successful, %d failed out of %d total rows",
                    result.getSuccessfulImports(), result.getFailedImports(), result.getTotalRows());

            return ResponseEntity.ok(ResponseData.<QuizExcelImportResultDto>builder()
                    .status(200)
                    .message(message)
                    .error(null)
                    .data(result)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<QuizExcelImportResultDto>builder()
                            .status(500)
                            .message("Failed to import quizzes from Excel")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    /**
     * Download Excel template for quiz import
     * 
     * @return Excel template file (.xlsx)
     * 
     * Excel template format:
     * - Row 0: Header row (Lesson ID, Type, Question, Answer, Status, Teacher Note, Option 1-4, Is Correct 1-4)
     * - Row 1: Instructions
     * - Row 2: Example data
     * - Row 3: Additional instructions
     * - Row 4+: User data (start from here)
     */
    @GetMapping(value = "/template", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> downloadQuizImportTemplate() {
        try {
            byte[] templateBytes = quizExcelTemplateGenerator.generateTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "quiz_import_template.xlsx");
            headers.setContentLength(templateBytes.length);
            // Add cache control headers
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(templateBytes);
        } catch (Exception e) {
            logger.error("Error generating quiz import template: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
