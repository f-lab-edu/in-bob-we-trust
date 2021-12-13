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

    public static ResponseEntity<ApiResult<?>> errorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(error(message, status), defaultHeaders(), status);
    }

    private static ApiResult<?> error(String message, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(message, status.value()));
    }

    public static ApiResult makeResponseBody(Object body) {
        if (body instanceof ApiResult) return (ApiResult) body;
        return new ApiResult<>(true, body, null);
    }
}
