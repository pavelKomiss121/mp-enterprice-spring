package ru.mentee.blog.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mentee.blog.api.dto.ErrorResponse;
import ru.mentee.blog.exception.PostNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(PostNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlePostNotFound(PostNotFoundException ex) {
    log.warn("Post not found: {}", ex.getMessage());
    return ErrorResponse.builder()
        .error("NOT_FOUND")
        .message(ex.getMessage())
        .timestamp(LocalDateTime.now())
        .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
    log.warn("Validation failed: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    return ErrorResponse.builder()
        .error("VALIDATION_ERROR")
        .message("Validation failed")
        .timestamp(LocalDateTime.now())
        .validationErrors(errors)
        .build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGeneral(Exception ex) {
    log.error("Unexpected error", ex);

    return ErrorResponse.builder()
        .error("INTERNAL_ERROR")
        .message("Something went wrong")
        .timestamp(LocalDateTime.now())
        .build();
  }
}
