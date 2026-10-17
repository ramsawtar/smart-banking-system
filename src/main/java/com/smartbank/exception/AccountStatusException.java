package com.smartbank.exception;

public class AccountStatusException extends RuntimeException {

    public AccountStatusException(String message) {
        super(message);
    }
}