package com.inbobwetrust.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ApiUtil {

    private static HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static <T> ResponseEntity<ApiResult<T>> successResponse(T body) {
        return new ResponseEntity<>(success(body), defaultHeaders(), HttpStatus.OK);
    }

    private static <T> ApiResult<T> success(T body) {
        return new ApiResult<>(true, body, null);
    }

    public static ResponseEntity<ApiResult<?>> errorResponse(
            Exception exception, HttpStatus status) {
        return new ResponseEntity<>(error(exception, status), defaultHeaders(), status);
    }

    private static ApiResult<?> error(Exception exception, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(exception.getMessage(), status.value()));
    }
}
