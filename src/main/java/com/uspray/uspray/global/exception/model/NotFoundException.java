package com.uspray.uspray.global.exception.model;

import com.uspray.uspray.global.exception.ErrorStatus;

public class NotFoundException extends CustomException{

    public NotFoundException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
