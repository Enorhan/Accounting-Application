package com.cydeo.exceptions;

public class ClientVendorNotFoundException extends RuntimeException {
    public ClientVendorNotFoundException(String message) {
        super(message);
    }
}