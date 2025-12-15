package com.devmam.taraacademyapi.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for Quiz Excel import result
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class QuizExcelImportResultDto implements Serializable {
    
    private int totalRows;
    private int successfulImports;
    private int failedImports;
    private List<ImportError> errors;
    
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class ImportError implements Serializable {
        private int rowNumber;
        private String message;
        private String quizQuestion; // Question text to help identify the problematic quiz
    }
}

