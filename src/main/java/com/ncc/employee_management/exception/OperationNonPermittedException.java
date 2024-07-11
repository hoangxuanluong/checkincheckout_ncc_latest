package com.ncc.employee_management.exception;

public class OperationNonPermittedException extends RuntimeException {

    public OperationNonPermittedException(String message) {
        super(message);
    }
}