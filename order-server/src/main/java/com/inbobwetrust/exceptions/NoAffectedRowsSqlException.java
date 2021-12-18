package com.inbobwetrust.exceptions;

public class NoAffectedRowsSqlException extends RuntimeException{
    public NoAffectedRowsSqlException() {
        super("No rows affected from operation");
    }
}
