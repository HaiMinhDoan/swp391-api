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
}