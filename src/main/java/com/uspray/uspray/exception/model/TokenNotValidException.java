package com.uspray.uspray.exception.model;

import com.uspray.uspray.exception.ErrorStatus;

public class TokenNotValidException extends CustomException {

    public TokenNotValidException(ErrorStatus errorStatus,
        String message) {
        super(errorStatus, message);
    }
}
