package com.inbobwetrust.exceptions;

public class EmptyResultSetSqlException extends RuntimeException {
    public EmptyResultSetSqlException() {
        super("Result is Empty.....");
    }
}
