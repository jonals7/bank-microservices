package com.bank.customer_service.exception;

public class DuplicateIdentificationException extends RuntimeException {

    public DuplicateIdentificationException(String identification) {
        super("A customer with identification " + identification + " already exists");
    }
}