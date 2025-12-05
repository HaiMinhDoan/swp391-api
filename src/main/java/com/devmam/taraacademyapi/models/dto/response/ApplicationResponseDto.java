package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Application;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Application}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ApplicationResponseDto implements Serializable {
    private final Integer id;
    private final Integer careerId;
    private final String careerTitle;
    private final String cvUrl;
    private final Integer status;
    private final Instant interviewDate;
    private final String note;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer isDeleted;
    private final String finalNote;
    private final String fullName;
    private final String email;
    private final String phone;
    private final Short gender;
    private final Instant oboardDate;
    private final String interviewType;
    private final String meetingLink;

    public static ApplicationResponseDto toDTO(Application application) {
        return ApplicationResponseDto.builder()
                .id(application.getId())
                .careerId(application.getCareer() != null ? application.getCareer().getId() : null)
                .careerTitle(application.getCareer() != null ? application.getCareer().getTitle() : null)
                .cvUrl(application.getCvUrl())
                .status(application.getStatus())
                .interviewDate(application.getInterviewDate())
                .note(application.getNote())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .isDeleted(application.getIsDeleted())
                .finalNote(application.getFinalNote())
                .fullName(application.getFullName())
                .email(application.getEmail())
                .phone(application.getPhone())
                .gender(application.getGender())
                .oboardDate(application.getOboardDate())
                .interviewType(application.getInterviewType())
                .meetingLink(application.getMeetingLink())
                .build();
    }

    public static Page<ApplicationResponseDto> convertPage(Page<Application> applicationPage) {
        List<ApplicationResponseDto> applicationResponseDTOs = applicationPage.getContent()
                .stream()
                .map(ApplicationResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                applicationResponseDTOs,
                applicationPage.getPageable(),
                applicationPage.getTotalElements()
        );
    }
}

