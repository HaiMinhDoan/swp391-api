package com.devmam.taraacademyapi.service.impl.utils;


import com.devmam.taraacademyapi.exception.customize.CommonException;
import com.devmam.taraacademyapi.models.entities.User;
import com.devmam.taraacademyapi.repository.UserRepository;
import com.devmam.taraacademyapi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    // Cache template
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    /**
     * Gửi email
     * @param to
     * @param subject
     * @param content
     * @throws CommonException
     */
    @Override
    public void sendEmail(String to, String subject, String content) throws CommonException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            logger.info("[sendEmail] Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("[sendEmail] Error sending email to: {}", to, e);
            throw new CommonException("Error sending email to: " + to, e);
        }
    }

    /**
     * Gửi email cho user theo UUID
     * @param uuid
     * @param subject
     * @param content
     * @throws EntityNotFoundException
     * @throws CommonException
     */
    @Override
    public void sendEmailToUser(String uuid, String subject, String content) throws EntityNotFoundException, CommonException {
        logger.info("[sendEmailToUser] Sending email to user: {}", uuid);
        Optional<User> findingUser = userRepository.findById(UUID.fromString(uuid));
        if (findingUser.isEmpty()) {
            logger.error("[sendEmailToUser] User not found for uuid: {}", uuid);
            throw new EntityNotFoundException("User not found for uuid: " + uuid);
        }
        try {
            sendEmail(findingUser.get().getEmail(), subject, content);
        } catch (CommonException e) {
            logger.error("[sendEmailToUser] Error sending email to user: {}", uuid, e);
            throw e;
        }
    }

    /**
     * Gửi email HTML với template và parameters
     *
     * @param to           Email người nhận
     * @param subject      Tiêu đề email
     * @param htmlTemplate HTML template với placeholders dạng {{key}}
     * @param parameters   Map chứa key-value để thay thế placeholders
     * @throws CommonException
     */
    public void sendHtmlEmail(String to, String subject, String htmlTemplate, Map<String, Object> parameters) throws CommonException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            // Replace placeholders in template
            String processedHtml = processTemplate(htmlTemplate, parameters);
            helper.setText(processedHtml, true); // true = isHtml

            mailSender.send(mimeMessage);
            logger.info("[sendHtmlEmail] HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("[sendHtmlEmail] Error creating HTML email for: {}", to, e);
            throw new CommonException("Error creating HTML email for: " + to, e);
        } catch (Exception e) {
            logger.error("[sendHtmlEmail] Error sending HTML email to: {}", to, e);
            throw new CommonException("Error sending HTML email to: " + to, e);
        }
    }

    /**
     * Gửi email HTML với template và parameters cho user theo UUID
     *
     * @param uuid         UUID của user
     * @param subject      Tiêu đề email
     * @param htmlTemplate HTML template với placeholders dạng {{key}}
     * @param parameters   Map chứa key-value để thay thế placeholders
     * @throws EntityNotFoundException
     * @throws CommonException
     */
    public void sendHtmlEmailToUser(String uuid, String subject, String htmlTemplate, Map<String, Object> parameters)
            throws EntityNotFoundException, CommonException {
        logger.info("[sendHtmlEmailToUser] Sending HTML email to user: {}", uuid);
        Optional<User> findingUser = userRepository.findById(UUID.fromString(uuid));
        if (findingUser.isEmpty()) {
            logger.error("[sendHtmlEmailToUser] User not found for uuid: {}", uuid);
            throw new EntityNotFoundException("User not found for uuid: " + uuid);
        }

        User user = findingUser.get();

        // Thêm thông tin user vào parameters (optional)
        if (parameters != null) {
            parameters.put("userName", user.getUsername() != null ? user.getUsername() : "");
            parameters.put("userEmail", user.getEmail() != null ? user.getEmail() : "");
        }

        try {
            sendHtmlEmail(user.getEmail(), subject, htmlTemplate, parameters);
        } catch (CommonException e) {
            logger.error("[sendHtmlEmailToUser] Error sending HTML email to user: {}", uuid, e);
            throw e;
        }
    }

    /**
     * Gửi email HTML từ template file với parameters
     * @param to Email người nhận
     * @param subject Tiêu đề email
     * @param templateFileName Tên file template trong resources/templates (không cần .html)
     * @param parameters Map chứa key-value để thay thế placeholders
     * @throws CommonException
     */
    public void sendHtmlEmailFromTemplate(String to, String subject, String templateFileName, Map<String, Object> parameters) throws CommonException {
        try {
            String htmlTemplate = loadTemplate(templateFileName);
            sendHtmlEmail(to, subject, htmlTemplate, parameters);
        } catch (Exception e) {
            logger.error("[sendHtmlEmailFromTemplate] Error sending email from template {} to: {}", templateFileName, to, e);
            throw new CommonException("Error sending email from template " + templateFileName + " to: " + to, e);
        }
    }

    /**
     * Gửi email HTML từ template file cho user theo UUID
     * @param uuid UUID của user
     * @param subject Tiêu đề email
     * @param templateFileName Tên file template trong resources/templates (không cần .html)
     * @param parameters Map chứa key-value để thay thế placeholders
     * @throws EntityNotFoundException
     * @throws CommonException
     */
    public void sendHtmlEmailFromTemplateToUser(String uuid, String subject, String templateFileName, Map<String, Object> parameters)
            throws EntityNotFoundException, CommonException {
        logger.info("[sendHtmlEmailFromTemplateToUser] Sending HTML email from template {} to user: {}", templateFileName, uuid);
        Optional<User> findingUser = userRepository.findById(UUID.fromString(uuid));
        if (findingUser.isEmpty()) {
            logger.error("[sendHtmlEmailFromTemplateToUser] User not found for uuid: {}", uuid);
            throw new EntityNotFoundException("User not found for uuid: " + uuid);
        }

        User user = findingUser.get();

        // Thêm thông tin user vào parameters (optional)
        if (parameters != null) {
            parameters.put("userName", user.getUsername() != null ? user.getUsername() : "");
            parameters.put("userEmail", user.getEmail() != null ? user.getEmail() : "");
        }

        try {
            sendHtmlEmailFromTemplate(user.getEmail(), subject, templateFileName, parameters);
        } catch (CommonException e) {
            logger.error("[sendHtmlEmailFromTemplateToUser] Error sending HTML email from template {} to user: {}", templateFileName, uuid, e);
            throw e;
        }
    }

    /**
     * Xử lý template bằng cách thay thế placeholders {{key}} bằng values
     *
     * @param template   HTML template
     * @param parameters Map chứa key-value
     * @return Processed HTML string
     */
    private String processTemplate(String template, Map<String, Object> parameters) {
        if (template == null || parameters == null || parameters.isEmpty()) {
            return template;
        }

        String result = template;

        // Pattern để tìm placeholders dạng {{key}}
        Pattern pattern = Pattern.compile("\\{\\{(\\w+)\\}\\}");
        Matcher matcher = pattern.matcher(template);

        while (matcher.find()) {
            String placeholder = matcher.group(0); // {{key}}
            String key = matcher.group(1); // key

            Object value = parameters.get(key);
            String replacement = value != null ? value.toString() : "";

            result = result.replace(placeholder, replacement);
        }

        return result;
    }


    /**
     * Đọc template từ file trong resources/templates
     * @param templateFileName Tên file (không cần .html)
     * @return HTML content string
     * @throws CommonException
     */
    public String loadTemplate(String templateFileName) throws CommonException {
        // Kiểm tra cache trước
        String cacheKey = templateFileName;
        if (templateCache.containsKey(cacheKey)) {
            logger.debug("[loadTemplate] Template {} loaded from cache", templateFileName);
            return templateCache.get(cacheKey);
        }

        try {
            // Thêm .html nếu chưa có
            String fileName = templateFileName.endsWith(".html") ? templateFileName : templateFileName + ".html";
            String templatePath = "templates/" + fileName;

            ClassPathResource resource = new ClassPathResource(templatePath);

            if (!resource.exists()) {
                logger.error("[loadTemplate] Template file not found: {}", templatePath);
                throw new CommonException("Template file not found: " + templatePath);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                String template = new String(bytes, StandardCharsets.UTF_8);

                // Cache template
                templateCache.put(cacheKey, template);
                logger.info("[loadTemplate] Template {} loaded and cached successfully", templateFileName);

                return template;
            }
        } catch (IOException e) {
            logger.error("[loadTemplate] Error loading template: {}", templateFileName, e);
            throw new CommonException("Error loading template: " + templateFileName, e);
        }
    }

    /**
     * Clear template cache (useful for development/testing)
     */
    public void clearTemplateCache() {
        templateCache.clear();
        logger.info("[clearTemplateCache] Template cache cleared");
    }

    /**
     * Clear specific template from cache
     * @param templateFileName Template name to clear
     */
    public void clearTemplate(String templateFileName) {
        templateCache.remove(templateFileName);
        logger.info("[clearTemplate] Template {} cleared from cache", templateFileName);
    }
}