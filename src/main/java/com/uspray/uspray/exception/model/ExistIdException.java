package com.uspray.uspray.exception.model;

import com.uspray.uspray.exception.ErrorStatus;

public class ExistIdException extends CustomException {

    public ExistIdException(ErrorStatus errorStatus,
        String message) {
        super(errorStatus, message);
    }

}
