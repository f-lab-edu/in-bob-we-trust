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
    private final Class cause;

    public static ResponseEntity<ApiResult<?>> errorResponse(
            String message, Throwable cause, HttpStatus status) {
        return new ResponseEntity<>(error(message, cause), defaultHeaders(), status);
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
        return new ApiResult<>(true, body, null);
    }

    private static ApiResult<?> error(String message, Throwable cause) {
        return new ApiResult<String>(false, message, cause.getClass());
    }
}
