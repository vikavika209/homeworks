package com.example.exception;

public class NoInformationHasBeenFoundException extends RuntimeException {
    public NoInformationHasBeenFoundException(String message) {
        super(message);
    }
}
