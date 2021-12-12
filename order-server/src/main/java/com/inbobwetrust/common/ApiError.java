package com.inbobwetrust.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
class ApiError {
    private final String message;
    private final int status;
}
