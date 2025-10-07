package com.devmam.taraacademyapi.models.dto.request;

import com.devmam.taraacademyapi.constant.enums.FilterLogicType;
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
    @Builder.Default
    FilterLogicType logicType = FilterLogicType.AND;
}
