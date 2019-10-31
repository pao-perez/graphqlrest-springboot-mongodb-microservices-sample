package com.paoperez.imageservice;

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
final class ImageExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ImageNotFoundException.class)
    final ResponseEntity<ImageErrorResponse> handleNotFoundException(final ImageNotFoundException ex,
            final WebRequest request) {
        ImageErrorResponse responseBody = ImageErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(ImageAlreadyExistsException.class)
    final ResponseEntity<ImageErrorResponse> handleAlreadyExistsException(final ImageAlreadyExistsException ex,
            final WebRequest request) {
        ImageErrorResponse responseBody = ImageErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.CONFLICT).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    final ResponseEntity<ImageErrorResponse> handleConstraintViolation(final ConstraintViolationException ex,
            final WebRequest request) {
        Collection<String> message = ex.getConstraintViolations().stream().map(x -> x.getMessage())
                .collect(Collectors.toList());

        ImageErrorResponse responseBody = ImageErrorResponse.builder().message(message.toString())
                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        Collection<String> message = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        ImageErrorResponse responseBody = ImageErrorResponse.builder().message(message.toString())
                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(Exception.class)
    final ResponseEntity<ImageErrorResponse> handleAllExceptions(final Exception ex, final WebRequest request) {
        ImageErrorResponse responseBody = ImageErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

}
