package com.uspray.uspray.exception.model;

import com.uspray.uspray.exception.ErrorStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorStatus errorStatus;

    public CustomException(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public Integer getHttpStatus() {
        return errorStatus.getHttpStatusCode();
    }
}
