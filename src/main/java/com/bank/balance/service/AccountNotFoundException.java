package com.bank.balance.service;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Synthetic account was not found");
    }
}
