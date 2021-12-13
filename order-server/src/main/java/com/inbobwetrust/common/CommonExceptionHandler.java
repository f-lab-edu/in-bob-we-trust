package com.inbobwetrust.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.inbobwetrust.common.ApiUtil.errorResponse;

@ControllerAdvice
public class CommonExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(Exception e) {
        return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
