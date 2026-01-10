package com.leeyujun.stockinsightapi.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException e){
        Map<String,Object> body = new HashMap<>();
        body.put("message",e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException e){
        Map<String,Object> body = new HashMap<>();
        body.put("message",e.getMessage());

        Map<String,String> errors = new HashMap<>();
        for(FieldError fe : e.getBindingResult().getFieldErrors()){
            errors.put(fe.getField(),fe.getDefaultMessage());
        }
        body.put("errors",errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialException(InvalidCredentialException e){
        Map<String, Object> body = new HashMap<>();
        body.put("message",e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
