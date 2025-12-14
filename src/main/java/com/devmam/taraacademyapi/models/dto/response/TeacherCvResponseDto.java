package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.TeacherCv;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.TeacherCv}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TeacherCvResponseDto implements Serializable {
    private final Integer id;
    private final String username;
    private final String email;
    private final String fullName;
    private final String phone;
    private final String avatarUrl;
    private final String userUsername;
    private final String title;
    private final String description;
    private final String cvUrl;
    private final String experience;
    private final String skills;
    private final String educations;
    private final String certificates;
    private final UUID createdById;
    private final String createdByUsername;
    private final String createdByFullName;
    private final String createdByPhone;
    private final String createdByAvatarUrl;
    private final String createdByUserUsername;
    private final String createdByEmail;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;
    private final String rejectReason;

    public static TeacherCvResponseDto toDTO(TeacherCv teacherCv) {
        return TeacherCvResponseDto.builder()
                .id(teacherCv.getId())
                .username(teacherCv.getUser() != null ? teacherCv.getUser().getUsername() : null)
                .email(teacherCv.getUser() != null ? teacherCv.getUser().getEmail() : null)
                .fullName(teacherCv.getUser() != null ? teacherCv.getUser().getFullName() : null)
                .avatarUrl(teacherCv.getUser() != null ? teacherCv.getUser().getAvt() : null)
                .username(teacherCv.getUser() != null ? teacherCv.getUser().getUsername() : null)
                .userUsername(teacherCv.getUser() != null ? teacherCv.getUser().getUsername() : null)
                .title(teacherCv.getTitle())
                .description(teacherCv.getDescription())
                .cvUrl(teacherCv.getCvUrl())
                .experience(teacherCv.getExperience())
                .skills(teacherCv.getSkills())
                .educations(teacherCv.getEducations())
                .certificates(teacherCv.getCertificates())
                .createdById(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getId() : null)
                .createdByUsername(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getUsername() : null)
                .createdByFullName(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getFullName() : null)
                .createdByPhone(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getPhone() : null)
                .createdByAvatarUrl(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getAvt() : null)
                .createdByUserUsername(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getUsername() : null)
                .createdByEmail(teacherCv.getCreatedBy() != null ? teacherCv.getCreatedBy().getEmail() : null)
                .createdAt(teacherCv.getCreatedAt())
                .updatedAt(teacherCv.getUpdatedAt())
                .status(teacherCv.getStatus())
                .isDeleted(teacherCv.getIsDeleted())
                .rejectReason(teacherCv.getRejectReason())
                .build();
    }

    public static Page<TeacherCvResponseDto> convertPage(Page<TeacherCv> teacherCvPage) {
        List<TeacherCvResponseDto> teacherCvResponseDTOs = teacherCvPage.getContent()
                .stream()
                .map(TeacherCvResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                teacherCvResponseDTOs,
                teacherCvPage.getPageable(),
                teacherCvPage.getTotalElements()
        );
    }
}
