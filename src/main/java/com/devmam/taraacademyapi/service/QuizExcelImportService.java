package com.devmam.taraacademyapi.service;

import com.devmam.taraacademyapi.models.dto.response.QuizExcelImportResultDto;
import com.devmam.taraacademyapi.models.entities.Quiz;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for importing Quiz data from Excel files
 */
public interface QuizExcelImportService {
    
    /**
     * Import quizzes from Excel file
     * @param file Excel file containing quiz data
     * @return Import result with success/failure details
     */
    QuizExcelImportResultDto importQuizzesFromExcel(MultipartFile file);
    
    /**
     * Save quiz and options in a separate transaction
     * This ensures that if one quiz fails, it doesn't rollback other successful imports
     * @param quiz Quiz entity to save
     * @param row Excel row containing option data
     */
    void saveQuizWithOptions(Quiz quiz, Row row);
}

