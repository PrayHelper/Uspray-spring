package com.uspray.uspray.external.client.oauth2.apple;

public class MyServerException extends RuntimeException {

    int status;

    String message;

    public MyServerException(int status, String message) {
        super(message);
        this.status = status;
    }
}
