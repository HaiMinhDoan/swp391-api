package com.devmam.taraacademyapi.service.impl.utils;

import com.devmam.taraacademyapi.models.dto.response.QuizExcelImportResultDto;
import com.devmam.taraacademyapi.models.entities.Lesson;
import com.devmam.taraacademyapi.models.entities.Quiz;
import com.devmam.taraacademyapi.models.entities.QuizOption;
import com.devmam.taraacademyapi.service.QuizExcelImportService;
import com.devmam.taraacademyapi.service.impl.entities.LessonService;
import com.devmam.taraacademyapi.service.impl.entities.QuizOptionService;
import com.devmam.taraacademyapi.service.impl.entities.QuizService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for importing Quiz data from Excel files
 */
@Service
public class QuizExcelImportServiceImpl implements QuizExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(QuizExcelImportServiceImpl.class);

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizOptionService quizOptionService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ApplicationContext applicationContext; // For self-injection to avoid circular dependency

    // Excel column indices
    private static final int COL_LESSON_ID = 0;
    private static final int COL_QUESTION = 1;
    private static final int COL_OPTION_1 = 2;
    private static final int COL_IS_CORRECT_1 = 3;
    private static final int COL_OPTION_2 = 4;
    private static final int COL_IS_CORRECT_2 = 5;
    private static final int COL_OPTION_3 = 6;
    private static final int COL_IS_CORRECT_3 = 7;
    private static final int COL_OPTION_4 = 8;
    private static final int COL_IS_CORRECT_4 = 9;

    @Override
    @Transactional
    public QuizExcelImportResultDto importQuizzesFromExcel(MultipartFile file) {
        logger.info("Starting Excel import process for file: {}", file.getOriginalFilename());
        List<QuizExcelImportResultDto.ImportError> errors = new ArrayList<>();
        int successfulImports = 0;
        int totalRows = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum();
            logger.info("Excel file loaded. Total rows: {}", totalRows);

            // Find header row (contains "Lesson ID")
            int headerRowIndex = findHeaderRow(sheet);
            logger.info("Header row found at index: {}", headerRowIndex);
            // Start from row after header (skip header row only)
            // If there's an instruction row (row 1) and example row (row 2), they will be skipped automatically
            int startRowIndex = headerRowIndex + 2;
            logger.info("Starting import from row index: {}", startRowIndex);

            for (int rowIndex = startRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                try {
                    logger.debug("Processing row {}: {}", rowIndex + 1, rowIndex);
                    
                    // Check if row is empty
                    if (isRowEmpty(row)) {
                        logger.debug("Row {} is empty, skipping", rowIndex + 1);
                        continue;
                    }

                    // Skip if it looks like a header row
                    String firstCellValue = getCellValueAsString(row.getCell(0));
                    if (firstCellValue != null && (firstCellValue.contains("Lesson ID") || 
                        firstCellValue.equalsIgnoreCase("Lesson ID"))) {
                        logger.debug("Row {} looks like header row, skipping", rowIndex + 1);
                        continue;
                    }

                    // Skip instruction row (contains "Lưu ý" or "Note")
                    String questionValue = getCellValueAsString(row.getCell(COL_QUESTION));
                    if (questionValue != null && (questionValue.contains("Lưu ý") || 
                        questionValue.contains("Note") || questionValue.contains("Lưu ý:"))) {
                        logger.debug("Row {} is instruction row, skipping", rowIndex + 1);
                        continue;
                    }

                    // Read quiz data from row
                    logger.debug("Creating quiz from row {}", rowIndex + 1);
                    Quiz quiz = createQuizFromRow(row);
                    
                    // Validate quiz with detailed error messages
                    logger.debug("Validating quiz from row {}", rowIndex + 1);
                    String validationError = validateQuizRow(row, quiz);
                    if (validationError != null) {
                        logger.warn("Validation failed for row {}: {}", rowIndex + 1, validationError);
                        errors.add(QuizExcelImportResultDto.ImportError.builder()
                                .rowNumber(rowIndex + 1)
                                .message(validationError)
                                .quizQuestion(getCellValueAsString(row.getCell(COL_QUESTION)))
                                .build());
                        continue;
                    }

                    // Save quiz and options in a separate transaction
                    logger.debug("Saving quiz from row {}", rowIndex + 1);
                    QuizExcelImportService self = applicationContext.getBean(QuizExcelImportService.class);
                    self.saveQuizWithOptions(quiz, row);
                    successfulImports++;
                    logger.info("Successfully imported quiz from row {}", rowIndex + 1);
                } catch (Exception e) {
                    logger.error("Error importing quiz at row {}: {}", rowIndex + 1, e.getMessage(), e);
                    logger.error("Stack trace: ", e);
                    errors.add(QuizExcelImportResultDto.ImportError.builder()
                            .rowNumber(rowIndex + 1)
                            .message("Error: " + e.getMessage())
                            .quizQuestion(getCellValueAsString(row.getCell(COL_QUESTION)))
                            .build());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", e.getMessage(), e);
            logger.error("Stack trace: ", e);
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }

        logger.info("Import completed. Total rows: {}, Successful: {}, Failed: {}", 
                totalRows, successfulImports, errors.size());
        
        return QuizExcelImportResultDto.builder()
                .totalRows(totalRows)
                .successfulImports(successfulImports)
                .failedImports(errors.size())
                .errors(errors)
                .build();
    }

    /**
     * Validate quiz row and return error message if invalid, null if valid
     */
    private String validateQuizRow(Row row, Quiz quiz) {
        // Validate Question
        String question = quiz.getQuestion();
        if (question == null || question.trim().isEmpty()) {
            return "Question là bắt buộc và không được để trống";
        }

        // Validate Lesson ID if provided
        Integer lessonId = getCellValueAsInteger(row.getCell(COL_LESSON_ID));
        if (lessonId != null) {
            if (!lessonService.exists(lessonId)) {
                return String.format("Lesson ID %d không tồn tại trong hệ thống", lessonId);
            }
        }

        // Validate options
        List<QuizOption> options = createQuizOptionsFromRow(row, quiz);
        
        // Check if at least one option exists
        long validOptionsCount = options.stream()
                .filter(opt -> opt.getContent() != null && !opt.getContent().trim().isEmpty())
                .count();
        
        if (validOptionsCount == 0) {
            return "Phải có ít nhất 1 option (Option 1-4)";
        }

        // Check if at least one option is correct
        boolean hasCorrectOption = options.stream()
                .filter(opt -> opt.getContent() != null && !opt.getContent().trim().isEmpty())
                .anyMatch(QuizOption::getIsCorrect);
        
        if (!hasCorrectOption) {
            return "Phải có ít nhất 1 option đúng (Is Correct = true/yes/1/đúng)";
        }

        // Validate option content if Is Correct is set
        for (int i = 0; i < options.size(); i++) {
            QuizOption option = options.get(i);
            Boolean isCorrect = option.getIsCorrect();
            String content = option.getContent();
            
            // If Is Correct is true but content is empty, it's invalid
            if (Boolean.TRUE.equals(isCorrect) && (content == null || content.trim().isEmpty())) {
                return String.format("Option %d có Is Correct = true nhưng nội dung trống", i + 1);
            }
        }

        return null; // Valid
    }

    private Quiz createQuizFromRow(Row row) {
        Quiz quiz = new Quiz();

        // Lesson ID
        Integer lessonId = getCellValueAsInteger(row.getCell(COL_LESSON_ID));
        if (lessonId != null) {
            Lesson lesson = lessonService.getOne(lessonId).orElse(null);
            quiz.setLesson(lesson);
        }

        // Type (always set to "Multiple Choice")
        quiz.setType("multiple choice");

        // Question
        quiz.setQuestion(getCellValueAsString(row.getCell(COL_QUESTION)));

        // Status (default to 1 - always set to 1)
        quiz.setStatus(1);

        // Teacher Note (not in Excel, set to null)
        quiz.setTeacherNote(null);

        // Set defaults
        quiz.setIsDeleted(0);
        quiz.setCreatedAt(Instant.now());
        quiz.setUpdatedAt(Instant.now());

        return quiz;
    }

    /**
     * Save quiz and options in a separate transaction
     * This ensures that if one quiz fails, it doesn't rollback other successful imports
     * Must be public for Spring AOP to work with @Transactional
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveQuizWithOptions(Quiz quiz, Row row) {
        try {
            logger.debug("Saving quiz: {}", quiz.getQuestion());
            // Save quiz
            Quiz savedQuiz = quizService.create(quiz);
            logger.debug("Quiz saved with ID: {}", savedQuiz.getId());

            // Read and save quiz options
            List<QuizOption> options = createQuizOptionsFromRow(row, savedQuiz);
            logger.debug("Created {} options for quiz", options.size());
            
            int savedOptions = 0;
            for (QuizOption option : options) {
                if (option.getContent() != null && !option.getContent().trim().isEmpty()) {
                    quizOptionService.create(option);
                    savedOptions++;
                }
            }
            logger.debug("Saved {} options for quiz ID: {}", savedOptions, savedQuiz.getId());
        } catch (Exception e) {
            logger.error("Error saving quiz with options. Question: {}", quiz.getQuestion(), e);
            logger.error("Stack trace: ", e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }

    private List<QuizOption> createQuizOptionsFromRow(Row row, Quiz quiz) {
        List<QuizOption> options = new ArrayList<>();

        // Process 4 option columns
        for (int optionIndex = 0; optionIndex < 4; optionIndex++) {
            int optionCol = COL_OPTION_1 + (optionIndex * 2);
            int isCorrectCol = optionCol + 1;

            String optionContent = getCellValueAsString(row.getCell(optionCol));
            if (optionContent == null || optionContent.trim().isEmpty()) {
                continue; // Skip empty options
            }

            Boolean isCorrect = getCellValueAsBoolean(row.getCell(isCorrectCol));

            QuizOption option = new QuizOption();
            option.setQuiz(quiz);
            option.setContent(optionContent);
            option.setIsCorrect(isCorrect != null ? isCorrect : false);
            option.setStatus(1);
            option.setIsDeleted(0);
            option.setCreatedAt(Instant.now());
            option.setUpdatedAt(Instant.now());

            options.add(option);
        }

        return options;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Convert numeric to string without decimal if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    if (value.isEmpty()) {
                        return null;
                    }
                    return Integer.parseInt(value);
                case FORMULA:
                    return (int) cell.getNumericCellValue();
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.warn("Error parsing integer from cell: {}", e.getMessage());
            return null;
        }
    }

    private Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim().toLowerCase();
                    return "true".equals(value) || "yes".equals(value) || "1".equals(value) || "đúng".equals(value);
                case NUMERIC:
                    return cell.getNumericCellValue() == 1;
                case FORMULA:
                    return cell.getBooleanCellValue();
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.warn("Error parsing boolean from cell: {}", e.getMessage());
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private int findHeaderRow(Sheet sheet) {
        // Look for header row containing "Lesson ID"
        for (int i = 0; i <= sheet.getLastRowNum() && i < 10; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(COL_LESSON_ID);
                String value = getCellValueAsString(cell);
                if (value != null && value.contains("Lesson ID")) {
                    return i;
                }
            }
        }
        // Default to row 0 if header not found
        return 0;
    }
}

