package com.uspray.uspray.exception.model;

import com.uspray.uspray.exception.ErrorStatus;

public class ExistEmailException extends CustomException {

    public ExistEmailException(ErrorStatus errorStatus,
        String message) {
        super(errorStatus, message);
    }

}
