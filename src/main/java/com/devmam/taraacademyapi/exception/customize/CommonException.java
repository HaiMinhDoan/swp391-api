package com.devmam.taraacademyapi.exception.customize;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonException extends RuntimeException {
    HttpStatus httpStatus;
    Object data;

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CommonException(String message) {
        super(message);
        httpStatus = HttpStatus.BAD_REQUEST;
    }
}
