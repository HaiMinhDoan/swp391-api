package com.devmam.taraacademyapi.service.impl.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class to generate Excel template for Quiz import
 */
@Component
public class QuizExcelTemplateGenerator {

    /**
     * Generate Excel template file for quiz import
     * @return Excel file as byte array
     */
    public byte[] generateTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Quiz Import Template");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle instructionStyle = createInstructionStyle(workbook);

            // Create header row (row 0)
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Lesson ID",
                "Question",
                "Option 1",
                "Is Correct 1",
                "Option 2",
                "Is Correct 2",
                "Option 3",
                "Is Correct 3",
                "Option 4",
                "Is Correct 4"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create instruction row
            Row instructionRow = sheet.createRow(1);
            createMergedCell(sheet, instructionRow, 0, 9, "Lưu ý: Question là bắt buộc. Type mặc định là 'Multiple Choice'. Status mặc định là 1. Is Correct có thể là: true/false, yes/no, 1/0, đúng/sai", instructionStyle);

            // Create example row (row 2)
            Row exampleRow = sheet.createRow(2);
            CellStyle exampleStyle = createExampleStyle(workbook);
            
            exampleRow.createCell(0).setCellValue(1); // Lesson ID
            exampleRow.createCell(1).setCellValue("Đâu là thủ đô của Việt Nam?"); // Question
            exampleRow.createCell(2).setCellValue("Hà Nội"); // Option 1
            exampleRow.createCell(3).setCellValue(true); // Is Correct 1
            exampleRow.createCell(4).setCellValue("Hồ Chí Minh"); // Option 2
            exampleRow.createCell(5).setCellValue(false); // Is Correct 2
            exampleRow.createCell(6).setCellValue("Đà Nẵng"); // Option 3
            exampleRow.createCell(7).setCellValue(false); // Is Correct 3
            exampleRow.createCell(8).setCellValue("Huế"); // Option 4
            exampleRow.createCell(9).setCellValue(false); // Is Correct 4

            // Apply example style to all cells
            for (int i = 0; i < 10; i++) {
                exampleRow.getCell(i).setCellStyle(exampleStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createInstructionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createExampleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        return style;
    }

    private void createMergedCell(Sheet sheet, Row row, int firstCol, int lastCol, String value, CellStyle style) {
        CellRangeAddress mergedRegion = new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol);
        sheet.addMergedRegion(mergedRegion);
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}

