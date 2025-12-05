package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.EmailHistory;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.EmailHistory}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EmailHistoryResponseDto implements Serializable {
    private final Integer id;
    private final String recipientEmail;
    private final String subject;
    private final String content;
    private final String status;
    private final String errorMessage;
    private final Instant createdAt;
    private final UUID createdById;
    private final String createdByUsername;
    private final String createdByFullName;
    private final String createdByEmail;
    private final Integer applyId;
    private final String applyFullName;
    private final String applyEmail;

    public static EmailHistoryResponseDto toDTO(EmailHistory emailHistory) {
        return EmailHistoryResponseDto.builder()
                .id(emailHistory.getId())
                .recipientEmail(emailHistory.getRecipientEmail())
                .subject(emailHistory.getSubject())
                .content(emailHistory.getContent())
                .status(emailHistory.getStatus())
                .errorMessage(emailHistory.getErrorMessage())
                .createdAt(emailHistory.getCreatedAt())
                .createdById(emailHistory.getCreatedBy() != null ? emailHistory.getCreatedBy().getId() : null)
                .createdByUsername(emailHistory.getCreatedBy() != null ? emailHistory.getCreatedBy().getUsername() : null)
                .createdByFullName(emailHistory.getCreatedBy() != null ? emailHistory.getCreatedBy().getFullName() : null)
                .createdByEmail(emailHistory.getCreatedBy() != null ? emailHistory.getCreatedBy().getEmail() : null)
                .applyId(emailHistory.getApply() != null ? emailHistory.getApply().getId() : null)
                .applyFullName(emailHistory.getApply() != null ? emailHistory.getApply().getFullName() : null)
                .applyEmail(emailHistory.getApply() != null ? emailHistory.getApply().getEmail() : null)
                .build();
    }

    public static Page<EmailHistoryResponseDto> convertPage(Page<EmailHistory> emailHistoryPage) {
        List<EmailHistoryResponseDto> emailHistoryResponseDTOs = emailHistoryPage.getContent()
                .stream()
                .map(EmailHistoryResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                emailHistoryResponseDTOs,
                emailHistoryPage.getPageable(),
                emailHistoryPage.getTotalElements()
        );
    }
}

