package com.capstone2024.scss.application.advice.controllers;

import com.capstone2024.scss.application.advice.exeptions.ForbiddenException;
import com.capstone2024.scss.application.advice.exeptions.InternalServerException;
import com.capstone2024.scss.application.advice.exeptions.NotFoundException;
import com.capstone2024.scss.application.advice.exeptions.BadRequestException;
import com.capstone2024.scss.application.common.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFoundException(NotFoundException exception) {
        return ResponseUtil.getResponse(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException exception) {
        return ResponseUtil.getResponse(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        if(exception.getErrors() != null) {
            return ResponseUtil.getResponse(exception.getErrors(), exception.getStatus());
        }
        return ResponseUtil.getResponse(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<Object> handleInternalServerException(InternalServerException internalServerException) {
        return ResponseUtil.getResponse(internalServerException.getMessage(), internalServerException.getStatus());
    }
}
