package com.paoperez.contentservice;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
final class ContentExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(ContentExceptionHandler.class);

  @ExceptionHandler(ContentNotFoundException.class)
  final ResponseEntity<ContentErrorResponse> handleNotFoundException(
      final ContentNotFoundException ex, final WebRequest request) {
    log.error("Not Found", ex);
    ContentErrorResponse responseBody =
        ContentErrorResponse.builder().message(ex.getLocalizedMessage())
            .timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND).build();

    return new ResponseEntity<>(responseBody, responseBody.getStatus());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  final ResponseEntity<ContentErrorResponse> handleConstraintViolation(
      final ConstraintViolationException ex, final WebRequest request) {
    Collection<String> message =
        ex.getConstraintViolations().stream().map(
            ConstraintViolation::getMessage).collect(Collectors.toList());
    log.error("Bad Request", ex);
    ContentErrorResponse responseBody = ContentErrorResponse.builder().message(message.toString())
        .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

    return new ResponseEntity<>(responseBody, responseBody.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status,
      final WebRequest request) {
    Collection<String> message = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage).collect(Collectors.toList());
    log.error("Bad Request", ex);
    ContentErrorResponse responseBody = ContentErrorResponse.builder().message(message.toString())
        .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

    return new ResponseEntity<>(responseBody, responseBody.getStatus());
  }

  @ExceptionHandler(Exception.class)
  final ResponseEntity<ContentErrorResponse> handleAllExceptions(final Exception ex,
      final WebRequest request) {
    log.error("Internal Server Error", ex);
    ContentErrorResponse responseBody = ContentErrorResponse.builder()
        .message(
            "There is an internal server error. We will look into it and update the site soon.")
        .timestamp(LocalDateTime.now()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    return new ResponseEntity<>(responseBody, responseBody.getStatus());
  }
}
