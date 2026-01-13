package com.leeyujun.stockinsightapi.common.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(){
        super("Invalid eamil or password");
    }
}
