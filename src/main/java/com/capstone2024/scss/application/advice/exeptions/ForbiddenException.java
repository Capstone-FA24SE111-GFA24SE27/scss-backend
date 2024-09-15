package com.capstone2024.scss.application.advice.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
public class ForbiddenException extends RuntimeException{
    private HttpStatus status = null;
    private BindingResult errors = null;

    public ForbiddenException(String message) {
        super(message);
        this.status = HttpStatus.FORBIDDEN;
    }

    public ForbiddenException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ForbiddenException(String message, BindingResult errors, HttpStatus status) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}
