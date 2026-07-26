package com.dsar.exception;

import com.dsar.workflow.InvalidTransitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
  public class GlobalExceptionHandler {

@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex) {
      return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

@ExceptionHandler(InvalidTransitionException.class)
    public ResponseEntity<Object> handleInvalidTransition(InvalidTransitionException ex) {
      return build(HttpStatus.CONFLICT, ex.getMessage());
    }

@ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleAuthFailure(UsernameNotFoundException ex) {
      return build(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex) {
      return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

@ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
      return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

private ResponseEntity<Object> build(HttpStatus status, String message) {
  Map<String, Object> body = new LinkedHashMap<>();
  body.put("timestamp", Instant.now());
  body.put("status", status.value());
  body.put("error", status.getReasonPhrase());
  body.put("message", message);
  return ResponseEntity.status(status).body(body);
}
  }
