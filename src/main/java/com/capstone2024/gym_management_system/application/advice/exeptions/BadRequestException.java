package com.capstone2024.gym_management_system.application.advice.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
public class BadRequestException extends RuntimeException{
    private HttpStatus status = null;
    private BindingResult errors = null;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BadRequestException(String message, BindingResult errors, HttpStatus status) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}
