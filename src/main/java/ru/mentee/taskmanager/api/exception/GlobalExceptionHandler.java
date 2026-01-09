/* @MENTEE_POWER (C)2026 */
package ru.mentee.taskmanager.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentee.taskmanager.api.generated.dto.ErrorResponse;
import ru.mentee.taskmanager.api.generated.dto.FieldError;
import ru.mentee.taskmanager.api.generated.dto.ValidationErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setCode("RESOURCE_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Invalid request body: {}", ex.getMessage());

        // Извлекаем информацию об ошибке из сообщения
        String errorMessage = ex.getMessage();
        String fieldName = extractFieldName(errorMessage);

        FieldError fieldError = new FieldError();
        fieldError.setField(fieldName != null ? fieldName : "request");
        fieldError.setMessage(errorMessage);
        fieldError.setRejectedValue(null);

        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setCode("VALIDATION_FAILED");
        response.setMessage("Validation failed for request");
        response.setErrors(List.of(fieldError));
        response.setTimestamp(OffsetDateTime.now());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    private String extractFieldName(String errorMessage) {
        // Пытаемся извлечь имя поля из сообщения об ошибке
        // Например: "Cannot construct instance of `CreateTaskRequest$PriorityEnum`" -> "priority"
        if (errorMessage != null) {
            if (errorMessage.contains("PriorityEnum")) {
                return "priority";
            }
            if (errorMessage.contains("StatusEnum")) {
                return "status";
            }
        }
        return null;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());

        List<FieldError> fieldErrors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error -> {
                                    FieldError fieldError = new FieldError();
                                    fieldError.setField(error.getField());
                                    fieldError.setMessage(error.getDefaultMessage());
                                    fieldError.setRejectedValue(
                                            error.getRejectedValue() != null
                                                    ? error.getRejectedValue().toString()
                                                    : null);
                                    return fieldError;
                                })
                        .collect(Collectors.toList());

        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setCode("VALIDATION_FAILED");
        response.setMessage("Validation failed for request");
        response.setErrors(fieldErrors);
        response.setTimestamp(OffsetDateTime.now());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: ", ex);

        ErrorResponse error = new ErrorResponse();
        error.setCode("INTERNAL_ERROR");
        error.setMessage("An unexpected error occurred");
        error.setTimestamp(OffsetDateTime.now());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
