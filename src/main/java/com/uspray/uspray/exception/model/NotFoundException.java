package com.uspray.uspray.exception.model;

import com.uspray.uspray.exception.ErrorStatus;

public class NotFoundException extends CustomException{

    public NotFoundException(ErrorStatus errorStatus,
        String message) {
        super(errorStatus, message);
    }
}
