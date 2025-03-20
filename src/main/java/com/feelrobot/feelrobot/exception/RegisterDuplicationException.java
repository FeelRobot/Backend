package com.feelrobot.feelrobot.exception;

import lombok.Getter;

@Getter
public class RegisterDuplicationException extends Exception {

    private int resultCode;

    public RegisterDuplicationException(String message) {
    super(message);
  }

    public RegisterDuplicationException(String message, int resultCode) {
        super(message);
        this.resultCode = resultCode;
    }
}
