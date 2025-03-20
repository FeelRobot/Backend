package com.feelrobot.feelrobot.exception;

import lombok.Getter;

@Getter
public class ResponseException extends Exception {

    private int resultCode;
    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, int resultCode) {
        super(message);
        this.resultCode = resultCode;
    }
}
