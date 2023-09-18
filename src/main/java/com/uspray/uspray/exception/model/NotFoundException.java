package com.uspray.uspray.exception.model;

import com.uspray.uspray.exception.ErrorStatus;

public class NotFoundException extends RuntimeException{
    public NotFoundException(ErrorStatus errorStatus, String message) {
        super(errorStatus.getMessage() + message);
    }
}
