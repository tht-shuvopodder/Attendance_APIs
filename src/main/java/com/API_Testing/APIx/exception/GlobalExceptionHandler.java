package com.API_Testing.APIx.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == java.time.LocalDate.class) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("⚠️ Invalid date format. Use yyyy-MM-dd.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("⚠️ Invalid parameter type.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("⚠️ Invalid data entry..! " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("⚠️ Unexpected error: " + ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("⚠️ Not found: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("⚠️ Validation failed: " + errorMsg);
    }

    public static class DeviceNotFoundException extends RuntimeException {
        public DeviceNotFoundException(String msg) {
            super(msg);
        }
    }

    public static class AdminNotFoundException extends RuntimeException {
        public AdminNotFoundException(String msg) {
            super(msg);
        }
    }


    public static class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(String msg) {
            super(msg);
        }
    }


    public static class EmployeeNotFoundException extends RuntimeException {
        public EmployeeNotFoundException(String msg) {
            super(msg);
        }
    }

    public static class EmployeeAlreadyExistsException extends RuntimeException {
        public EmployeeAlreadyExistsException(String msg) {
            super(msg);
        }
    }

    public static class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String msg) {
            super(msg);
        }
    }
}
