package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Contact;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Contact}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ContactResponseDto implements Serializable {
    private final Integer id;
    private final String fullName;
    private final String phone;
    private final String email;
    private final String company;
    private final String personalRole;
    private final String subject;
    private final String message;
    private final Integer servicesId;
    private final String servicesName;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static ContactResponseDto toDTO(Contact contact) {
        return ContactResponseDto.builder()
                .id(contact.getId())
                .fullName(contact.getFullName())
                .phone(contact.getPhone())
                .email(contact.getEmail())
                .company(contact.getCompany())
                .personalRole(contact.getPersonalRole())
                .subject(contact.getSubject())
                .message(contact.getMessage())
                .servicesId(contact.getServices() != null ? contact.getServices().getId() : null)
                .servicesName(contact.getServices() != null ? contact.getServices().getName() : null)
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .status(contact.getStatus())
                .isDeleted(contact.getIsDeleted())
                .build();
    }

    public static Page<ContactResponseDto> convertPage(Page<Contact> contactPage) {
        List<ContactResponseDto> contactResponseDTOs = contactPage.getContent()
                .stream()
                .map(ContactResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                contactResponseDTOs,
                contactPage.getPageable(),
                contactPage.getTotalElements()
        );
    }
}