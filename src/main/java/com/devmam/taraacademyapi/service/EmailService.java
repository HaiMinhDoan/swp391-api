package com.devmam.taraacademyapi.service;

import com.devmam.taraacademyapi.exception.customize.CommonException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;

import java.util.Map;

public interface EmailService {
    void sendEmail(String to, String subject, String content) throws MessagingException;
    void sendEmailToUser(String uuid, String subject, String content) throws EntityNotFoundException, CommonException;
    void sendHtmlEmail(String to, String subject, String htmlTemplate, Map<String, Object> parameters) throws CommonException;
    void sendHtmlEmailToUser(String uuid, String subject, String htmlTemplate, Map<String, Object> parameters);
    void sendHtmlEmailFromTemplate(String to, String subject, String templateFileName, Map<String, Object> parameters) throws CommonException;
    void sendHtmlEmailFromTemplateToUser(String uuid, String subject, String templateFileName, Map<String, Object> parameters)
            throws EntityNotFoundException, CommonException;
    String loadTemplate(String templateFileName) throws CommonException;
    void clearTemplateCache();
    void clearTemplate(String templateFileName);
    
    /**
     * Gửi email và lưu vào EmailHistory
     * @param to Email người nhận
     * @param subject Tiêu đề email
     * @param content Nội dung email (có thể là HTML)
     * @param isHtml true nếu content là HTML
     * @param parameters Parameters để thay thế trong template (nếu có)
     * @param createdById ID của user gửi email (optional)
     * @param applyId ID của application liên quan (optional)
     * @return EmailHistory đã được lưu
     * @throws CommonException
     */
    com.devmam.taraacademyapi.models.entities.EmailHistory sendEmailAndSaveHistory(
            String to, String subject, String content, Boolean isHtml,
            java.util.Map<String, Object> parameters, java.util.UUID createdById, Integer applyId
    ) throws CommonException;
}