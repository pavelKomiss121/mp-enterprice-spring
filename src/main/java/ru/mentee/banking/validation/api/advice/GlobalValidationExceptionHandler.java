/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.mentee.banking.validation.api.dto.*;

@RestControllerAdvice
@Slf4j
public class GlobalValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.warn("Validation failed for {}: {}", request.getRequestURI(), ex.getMessage());

        List<FieldErrorDto> fieldErrors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error ->
                                        FieldErrorDto.builder()
                                                .field(error.getField())
                                                .code(error.getCode())
                                                .message(error.getDefaultMessage())
                                                .rejectedValue(error.getRejectedValue())
                                                .build())
                        .toList();

        List<GlobalErrorDto> globalErrors =
                ex.getBindingResult().getGlobalErrors().stream()
                        .map(
                                error ->
                                        GlobalErrorDto.builder()
                                                .objectName(error.getObjectName())
                                                .code(error.getCode())
                                                .message(error.getDefaultMessage())
                                                .build())
                        .toList();

        ValidationErrorResponse response =
                ValidationErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Failed")
                        .message("Invalid input parameters")
                        .path(request.getRequestURI())
                        .fieldErrors(fieldErrors)
                        .globalErrors(globalErrors)
                        .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        log.warn("Constraint violation for {}: {}", request.getRequestURI(), ex.getMessage());

        List<FieldErrorDto> fieldErrors =
                ex.getConstraintViolations().stream()
                        .map(
                                violation ->
                                        FieldErrorDto.builder()
                                                .field(
                                                        extractFieldName(
                                                                violation.getPropertyPath()))
                                                .message(violation.getMessage())
                                                .rejectedValue(violation.getInvalidValue())
                                                .build())
                        .toList();

        ValidationErrorResponse response =
                ValidationErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Constraint Violation")
                        .message("Validation failed")
                        .path(request.getRequestURI())
                        .fieldErrors(fieldErrors)
                        .globalErrors(new ArrayList<>())
                        .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        log.warn("Malformed JSON for {}: {}", request.getRequestURI(), ex.getMessage());

        String message = "Malformed JSON request";
        if (ex.getMessage() != null && ex.getMessage().contains("Cannot deserialize")) {
            message = ex.getMessage().split("\n")[0]; // Берем только первую строку
        }

        ValidationErrorResponse response =
                ValidationErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Malformed JSON")
                        .message(message)
                        .path(request.getRequestURI())
                        .fieldErrors(new ArrayList<>())
                        .globalErrors(new ArrayList<>())
                        .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String extractFieldName(Path propertyPath) {
        String pathStr = propertyPath.toString();
        int lastDot = pathStr.lastIndexOf('.');
        return lastDot >= 0 ? pathStr.substring(lastDot + 1) : pathStr;
    }
}
