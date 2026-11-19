package com.bank.balance.service;

public class InvalidAdditionalDataException extends RuntimeException {
    public InvalidAdditionalDataException(String message) {
        super(message);
    }
}
