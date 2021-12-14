package com.inbobwetrust.aop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class ApiResult<T> {
    private final boolean success;
    private final T body;

    public static ResponseEntity<ApiResult<?>> errorResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(error(message), defaultHeaders(), status);
    }

    public static <T> ApiResult makeFromResponseBody(T body) {
        if (body instanceof ApiResult) return (ApiResult) body;
        return success(body);
    }

    private static HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static <T> ApiResult success(T body) {
        return new ApiResult<>(true, body);
    }

    private static ApiResult<?> error(String message) {
        return new ApiResult<>(false, message);
    }
}
