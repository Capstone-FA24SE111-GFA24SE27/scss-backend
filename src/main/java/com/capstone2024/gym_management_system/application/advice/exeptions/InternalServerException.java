package com.capstone2024.gym_management_system.application.advice.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerException extends RuntimeException{
    private HttpStatus status = null;

    public InternalServerException() {
        super("Internal Server Error");
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public InternalServerException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public InternalServerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
