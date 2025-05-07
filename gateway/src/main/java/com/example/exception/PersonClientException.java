package com.example.exception;

public class PersonClientException extends RuntimeException {
    public PersonClientException(String message) {
        super(message);
    }
}
