package com.bank.account_service.exception;

public class DuplicateAccountNumberException extends RuntimeException {

    public DuplicateAccountNumberException(String accountNumber) {
        super("An account with number " + accountNumber + " already exists");
    }
}