package com.paoperez.avatarservice;

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
final class AvatarExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AvatarNotFoundException.class)
    final ResponseEntity<AvatarErrorResponse> handleNotFoundException(final AvatarNotFoundException ex,
            final WebRequest request) {
        AvatarErrorResponse responseBody = AvatarErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(AvatarAlreadyExistsException.class)
    final ResponseEntity<AvatarErrorResponse> handleAlreadyExistsException(final AvatarAlreadyExistsException ex,
            final WebRequest request) {
        AvatarErrorResponse responseBody = AvatarErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.CONFLICT).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    final ResponseEntity<AvatarErrorResponse> handleConstraintViolation(final ConstraintViolationException ex,
            final WebRequest request) {
        Collection<String> message = ex.getConstraintViolations().stream().map(x -> x.getMessage())
                .collect(Collectors.toList());

        AvatarErrorResponse responseBody = AvatarErrorResponse.builder().message(message.toString())
                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        Collection<String> message = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        AvatarErrorResponse responseBody = AvatarErrorResponse.builder().message(message.toString())
                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

    @ExceptionHandler(Exception.class)
    final ResponseEntity<AvatarErrorResponse> handleAllExceptions(final Exception ex, final WebRequest request) {
        AvatarErrorResponse responseBody = AvatarErrorResponse.builder().message(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return new ResponseEntity<>(responseBody, responseBody.getStatus());
    }

}
