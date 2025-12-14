package com.devmam.taraacademyapi.models.dto.response;

import com.devmam.taraacademyapi.models.entities.Tran;
import com.devmam.taraacademyapi.models.entities.UserCourse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for {@link com.devmam.taraacademyapi.models.entities.Tran}
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TranResponseDto implements Serializable {
    private final Integer id;
    private final UUID userId;
    private final String userUsername;
    private final Integer courseId;
    private final String courseTitle;
    private final BigDecimal amount;
    private final String paymentMethod;
//    private final String transactionId;
//    private final Instant transactionDate;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer status;
    private final Integer isDeleted;

    public static TranResponseDto toDTO(Tran tran) {
        if (tran == null) return null;
        Set<UserCourse> userCourses = null;
        Integer courseId = null;
        String courseTitle = null;
        if (tran.getUserCourses() != null && !tran.getUserCourses().isEmpty()) {
            userCourses = tran.getUserCourses();
            //Lấy phần tử đầu của set
            courseId = userCourses.iterator().next().getCourse().getId();
            courseTitle = userCourses.iterator().next().getCourse().getTitle();
        }
        return TranResponseDto.builder()
                .id(tran.getId())
                .userId(tran.getUser() != null ? tran.getUser().getId() : null)
                .userUsername(tran.getUser() != null ? tran.getUser().getUsername() : null)
                .courseId(courseId)
                .courseTitle(courseTitle)
                .amount(tran.getAmount())
//                .paymentMethod(tran.getPaymentMethod())
//                .transactionId(tran.getTransactionId())
//                .transactionDate(tran.getTransactionDate())
                .createdAt(tran.getCreatedAt())
                .updatedAt(tran.getUpdatedAt())
                .status(tran.getStatus())
                .isDeleted(tran.getIsDeleted())
                .build();
    }

    public static Page<TranResponseDto> convertPage(Page<Tran> tranPage) {
        List<TranResponseDto> tranResponseDTOs = tranPage.getContent()
                .stream()
                .map(TranResponseDto::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                tranResponseDTOs,
                tranPage.getPageable(),
                tranPage.getTotalElements()
        );
    }

    public static List<TranResponseDto> convertList(List<Tran> tranList) {
        return tranList.stream()
                .map(TranResponseDto::toDTO)
                .collect(Collectors.toList());
    }
}
