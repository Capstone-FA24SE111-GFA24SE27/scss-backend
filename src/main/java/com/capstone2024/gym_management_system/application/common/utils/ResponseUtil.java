package com.capstone2024.gym_management_system.application.common.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating standardized HTTP responses.
 */
public class ResponseUtil {

    /**
     * Creates a ResponseEntity containing the given content and HTTP status.
     *
     * @param content the content to be included in the response body
     * @param status  the HTTP status of the response
     * @return a ResponseEntity containing the content and status
     */
    public static ResponseEntity<Object> getResponse(Object content, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("status", status.value());

        return new ResponseEntity<>(map, status);
    }

    /**
     * Creates a ResponseEntity containing the custom content and HTTP status.
     *
     * @param map the content to be included in the response body
     * @param status  the HTTP status of the response
     * @return a ResponseEntity containing the content and status
     */
    public static ResponseEntity<Object> getResponse(HashMap<String, Object> map, HttpStatus status) {
        return new ResponseEntity<>(map, status);
    }

    /**
     * Creates a ResponseEntity containing error details from an exception and HTTP status.
     *
     * @param error  the exception from which to extract the error message
     * @param status the HTTP status of the response
     * @return a ResponseEntity containing the error message and status
     */
    public static ResponseEntity<Object> getResponse(Exception error, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put("errors", error.getMessage());
        map.put("status", status.value());

        return new ResponseEntity<>(map, status);
    }

    /**
     * Creates a ResponseEntity containing validation error messages from a BindingResult and HTTP status.
     *
     * @param errors the BindingResult containing validation errors
     * @param status the HTTP status of the response
     * @return a ResponseEntity containing the validation errors and status
     */
    public static ResponseEntity<Object> getResponse(BindingResult errors, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put("errors", ErrorUtil.getBindingResultErrorMessages(errors));
        map.put("status", status.value());

        return new ResponseEntity<>(map, status);
    }

    /**
     * Creates a ResponseEntity containing a message and HTTP status.
     *
     * @param status  the HTTP status of the response
     * @param message the message to be included in the response body
     * @return a ResponseEntity containing the message and status
     */
    public static ResponseEntity<Object> getResponse(String message, HttpStatus status) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());

        return new ResponseEntity<>(map, status);
    }
}
