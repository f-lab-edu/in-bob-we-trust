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

    private static <T> ApiResult<T> success(T body) {
        return new ApiResult<>(true, body);
    }

    public static ResponseEntity<ApiResult<?>> errorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(error(message), defaultHeaders(), status);
    }

    private static ApiResult<?> error(String message) {
        return new ApiResult<>(false, message);
    }

    public static ApiResult makeResponseBody(Object body) {
        if (body instanceof ApiResult) return (ApiResult) body;
        return success(body);
    }
}
