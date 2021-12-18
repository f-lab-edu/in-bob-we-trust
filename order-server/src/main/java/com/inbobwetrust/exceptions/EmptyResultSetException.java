package com.inbobwetrust.exceptions;

public class EmptyResultSetException extends RuntimeException {
    public EmptyResultSetException() {
        super("Result is Empty.....");
    }
}
