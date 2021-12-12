package com.inbobwetrust.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {
    private final boolean success;
    private final T response;
    private final ApiError error;
}
