package com.devmam.taraacademyapi.controller;

import com.devmam.taraacademyapi.models.dto.request.SendEmailRequestDto;
import com.devmam.taraacademyapi.models.dto.response.EmailHistoryResponseDto;
import com.devmam.taraacademyapi.models.dto.response.ResponseData;
import com.devmam.taraacademyapi.models.entities.EmailHistory;
import com.devmam.taraacademyapi.service.EmailService;
import com.devmam.taraacademyapi.service.JwtService;
import com.devmam.taraacademyapi.service.impl.entities.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@PreAuthorize("permitAll()")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    /**
     * Gửi email và lưu vào EmailHistory
     * Hỗ trợ cả plain text và HTML
     */
    @PostMapping("/send")
    public ResponseEntity<ResponseData<EmailHistoryResponseDto>> sendEmail(
            @Valid @RequestBody SendEmailRequestDto request) {
        try {
            // Get current user from token if createdById not provided
            java.util.UUID createdById = request.getCreatedById();
            if (createdById == null) {
                String currentUserEmail = jwtService.getCurrentUserId();
                if (currentUserEmail != null) {
                    java.util.Optional<com.devmam.taraacademyapi.models.entities.User> currentUser = 
                            userService.findByEmail(currentUserEmail);
                    if (currentUser.isPresent()) {
                        createdById = currentUser.get().getId();
                    }
                }
            }

            // Send email and save to history
            EmailHistory emailHistory = emailService.sendEmailAndSaveHistory(
                    request.getRecipientEmail(),
                    request.getSubject(),
                    request.getContent(),
                    request.getIsHtml() != null ? request.getIsHtml() : false,
                    request.getParameters(),
                    createdById,
                    request.getApplyId()
            );

            EmailHistoryResponseDto responseDto = EmailHistoryResponseDto.toDTO(emailHistory);

            return ResponseEntity.ok(ResponseData.<EmailHistoryResponseDto>builder()
                    .status(200)
                    .message("Email sent successfully")
                    .error(null)
                    .data(responseDto)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.<EmailHistoryResponseDto>builder()
                            .status(500)
                            .message("Failed to send email")
                            .error(e.getMessage())
                            .data(null)
                            .build());
        }
    }
}

