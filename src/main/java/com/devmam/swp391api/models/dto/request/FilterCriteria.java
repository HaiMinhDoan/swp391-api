package com.devmam.swp391api.models.dto.request;

import com.devmam.taraacademyapi.constant.enums.FilterOperation;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterCriteria {
    String fieldName;
    FilterOperation operation;
    Object value;
}
