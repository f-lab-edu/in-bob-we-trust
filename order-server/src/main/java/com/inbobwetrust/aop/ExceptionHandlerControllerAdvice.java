package com.inbobwetrust.aop;

import static com.inbobwetrust.aop.ApiResult.errorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(Exception e) {
        return errorResponse(e.getMessage(), e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(
            value = {
                HttpMessageNotReadableException.class,
                ConstraintViolationException.class,
                MethodArgumentNotValidException.class
            })
    public ResponseEntity<?> handleBadRequestContent(Exception e) {
        return errorResponse(e.getMessage(), e, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> handleUnsupportedMediaType(Exception e) {
        return errorResponse(e.getMessage(), e, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleException(Exception e) {
        return errorResponse(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
