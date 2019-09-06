package com.paoperez.categoryservice;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class CategoryExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CategoryNotFoundException.class)
    final ResponseEntity<CategoryErrorResponse> handleNotFoundException(CategoryNotFoundException ex,
            WebRequest request) {
        CategoryErrorResponse responseBody = CategoryErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    final ResponseEntity<CategoryErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
            WebRequest request) {
        Collection<String> message = ex.getConstraintViolations().stream().map(x -> x.getMessage())
                .collect(Collectors.toList());

        CategoryErrorResponse responseBody = CategoryErrorResponse.builder().message(message.toString())
                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Collection<String> message = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        CategoryErrorResponse responseBody = CategoryErrorResponse.builder().message(message.toString())
                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(Exception.class)
    final ResponseEntity<CategoryErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        CategoryErrorResponse responseBody = CategoryErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

}
