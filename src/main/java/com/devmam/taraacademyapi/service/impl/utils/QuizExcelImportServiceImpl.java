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
import org.springframework.stereotype.Service;
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

    // Excel column indices
    private static final int COL_LESSON_ID = 0;
    private static final int COL_TYPE = 1;
    private static final int COL_QUESTION = 2;
    private static final int COL_ANSWER = 3;
    private static final int COL_STATUS = 4;
    private static final int COL_TEACHER_NOTE = 5;
    private static final int COL_OPTION_1 = 6;
    private static final int COL_IS_CORRECT_1 = 7;
    private static final int COL_OPTION_2 = 8;
    private static final int COL_IS_CORRECT_2 = 9;
    private static final int COL_OPTION_3 = 10;
    private static final int COL_IS_CORRECT_3 = 11;
    private static final int COL_OPTION_4 = 12;
    private static final int COL_IS_CORRECT_4 = 13;

    @Override
    @Transactional
    public QuizExcelImportResultDto importQuizzesFromExcel(MultipartFile file) {
        List<QuizExcelImportResultDto.ImportError> errors = new ArrayList<>();
        int successfulImports = 0;
        int totalRows = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            totalRows = sheet.getLastRowNum();

            // Find header row (contains "Lesson ID")
            int headerRowIndex = findHeaderRow(sheet);
            int startRowIndex = headerRowIndex + 1;

            for (int rowIndex = startRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                try {
                    // Check if row is empty
                    if (isRowEmpty(row)) {
                        continue;
                    }

                    // Skip if it looks like a header row
                    String firstCellValue = getCellValueAsString(row.getCell(0));
                    if (firstCellValue != null && (firstCellValue.contains("Lesson ID") || 
                        firstCellValue.equalsIgnoreCase("Lesson ID"))) {
                        continue;
                    }

                    // Read quiz data from row
                    Quiz quiz = createQuizFromRow(row);
                    
                    // Validate quiz
                    if (quiz.getQuestion() == null || quiz.getQuestion().trim().isEmpty()) {
                        errors.add(QuizExcelImportResultDto.ImportError.builder()
                                .rowNumber(rowIndex + 1)
                                .message("Question is required")
                                .quizQuestion(getCellValueAsString(row.getCell(COL_QUESTION)))
                                .build());
                        continue;
                    }

                    // Save quiz
                    Quiz savedQuiz = quizService.create(quiz);

                    // Read and save quiz options
                    List<QuizOption> options = createQuizOptionsFromRow(row, savedQuiz);
                    for (QuizOption option : options) {
                        if (option.getContent() != null && !option.getContent().trim().isEmpty()) {
                            quizOptionService.create(option);
                        }
                    }

                    successfulImports++;
                } catch (Exception e) {
                    logger.error("Error importing quiz at row {}: {}", rowIndex + 1, e.getMessage(), e);
                    errors.add(QuizExcelImportResultDto.ImportError.builder()
                            .rowNumber(rowIndex + 1)
                            .message("Error: " + e.getMessage())
                            .quizQuestion(getCellValueAsString(row.getCell(COL_QUESTION)))
                            .build());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", e.getMessage(), e);
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }

        return QuizExcelImportResultDto.builder()
                .totalRows(totalRows)
                .successfulImports(successfulImports)
                .failedImports(errors.size())
                .errors(errors)
                .build();
    }

    private Quiz createQuizFromRow(Row row) {
        Quiz quiz = new Quiz();

        // Lesson ID
        Integer lessonId = getCellValueAsInteger(row.getCell(COL_LESSON_ID));
        if (lessonId != null) {
            Lesson lesson = lessonService.getOne(lessonId).orElse(null);
            quiz.setLesson(lesson);
        }

        // Type
        quiz.setType(getCellValueAsString(row.getCell(COL_TYPE)));

        // Question
        quiz.setQuestion(getCellValueAsString(row.getCell(COL_QUESTION)));

        // Answer
        quiz.setAnswer(getCellValueAsString(row.getCell(COL_ANSWER)));

        // Status (default to 1 if not provided)
        Integer status = getCellValueAsInteger(row.getCell(COL_STATUS));
        quiz.setStatus(status != null ? status : 1);

        // Teacher Note
        quiz.setTeacherNote(getCellValueAsString(row.getCell(COL_TEACHER_NOTE)));

        // Set defaults
        quiz.setIsDeleted(0);
        quiz.setCreatedAt(Instant.now());
        quiz.setUpdatedAt(Instant.now());

        return quiz;
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

