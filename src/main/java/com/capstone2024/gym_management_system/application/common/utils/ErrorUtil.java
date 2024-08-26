package com.capstone2024.gym_management_system.application.common.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handle error
 */
public class ErrorUtil {

    /**
     * Retrieves all error messages from the given BindingResult and stores them in a map.
     *
     * This method iterates over the field errors in the provided BindingResult and collects
     * them into a map where the key is the field name and the value is the default error message.
     *
     * @param errors the BindingResult containing validation errors
     * @return a map containing field names as keys and corresponding error messages as values
     */
    public static Map<String, Object> getBindingResultErrorMessages(BindingResult errors){
        Map<String, Object> errorsHolder = new HashMap<>();
        for(FieldError err: errors.getFieldErrors()) {
            errorsHolder.put(err.getField() ,err.getDefaultMessage());
        }
        return errorsHolder;
    }
}
