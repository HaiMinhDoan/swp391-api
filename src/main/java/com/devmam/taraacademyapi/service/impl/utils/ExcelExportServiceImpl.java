package com.devmam.taraacademyapi.service.impl.utils;

import com.devmam.taraacademyapi.models.entities.Contact;
import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.service.ExcelExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] exportContactsToExcel(List<Contact> contacts) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Contacts");
            
            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Full Name", "Email", "Phone", "Company", "Personal Role", 
                               "Subject", "Message", "Service Name", "Status", "Created At", "Updated At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Contact contact : contacts) {
                Row row = sheet.createRow(rowNum++);
                
                int colNum = 0;
                createCell(row, colNum++, contact.getId(), dataStyle);
                createCell(row, colNum++, contact.getFullName(), dataStyle);
                createCell(row, colNum++, contact.getEmail(), dataStyle);
                createCell(row, colNum++, contact.getPhone(), dataStyle);
                createCell(row, colNum++, contact.getCompany(), dataStyle);
                createCell(row, colNum++, contact.getPersonalRole(), dataStyle);
                createCell(row, colNum++, contact.getSubject(), dataStyle);
                createCell(row, colNum++, contact.getMessage(), dataStyle);
                createCell(row, colNum++, contact.getServices() != null ? contact.getServices().getName() : "", dataStyle);
                createCell(row, colNum++, contact.getStatus() != null ? contact.getStatus() : 0, dataStyle);
                createDateCell(row, colNum++, contact.getCreatedAt(), dateStyle);
                createDateCell(row, colNum++, contact.getUpdatedAt(), dateStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting contacts to Excel", e);
        }
    }

    @Override
    public byte[] exportTransactionsToExcel(List<Tran> transactions) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transactions");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "User ID", "User Email", "User Name", "Amount", "Method", 
                               "Response Code", "Status", "Detail", "Created At", "Updated At"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Tran transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                
                int colNum = 0;
                createCell(row, colNum++, transaction.getId(), dataStyle);
                createCell(row, colNum++, transaction.getUser() != null ? transaction.getUser().getId().toString() : "", dataStyle);
                createCell(row, colNum++, transaction.getUser() != null ? transaction.getUser().getEmail() : "", dataStyle);
                createCell(row, colNum++, transaction.getUser() != null ? transaction.getUser().getFullName() : "", dataStyle);
                createCurrencyCell(row, colNum++, transaction.getAmount(), currencyStyle);
                createCell(row, colNum++, transaction.getMethod(), dataStyle);
                createCell(row, colNum++, transaction.getResponseCode(), dataStyle);
                createCell(row, colNum++, transaction.getStatus() != null ? transaction.getStatus() : 0, dataStyle);
                
                // Detail as JSON string
                String detailStr = "";
                if (transaction.getDetail() != null) {
                    detailStr = transaction.getDetail().toString();
                }
                createCell(row, colNum++, detailStr, dataStyle);
                
                createDateCell(row, colNum++, transaction.getCreatedAt(), dateStyle);
                createDateCell(row, colNum++, transaction.getUpdatedAt(), dateStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error exporting transactions to Excel", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int column, Integer value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value);
        } else {
            cell.setCellValue(0);
        }
        cell.setCellStyle(style);
    }

    private void createCurrencyCell(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0.0);
        }
        cell.setCellStyle(style);
    }

    private void createDateCell(Row row, int column, Instant date, CellStyle style) {
        Cell cell = row.createCell(column);
        if (date != null) {
            cell.setCellValue(date.atZone(ZoneId.systemDefault()).toLocalDateTime());
            cell.setCellStyle(style);
        } else {
            cell.setCellValue("");
        }
    }
}

