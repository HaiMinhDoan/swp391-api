package com.devmam.taraacademyapi.service;

import com.devmam.taraacademyapi.models.dto.response.QuizExcelImportResultDto;
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
}

