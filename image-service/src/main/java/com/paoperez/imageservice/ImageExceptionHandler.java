package com.paoperez.imageservice;

import java.time.LocalDateTime;
import java.util.List;
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
class ImageExceptionHandler extends ResponseEntityExceptionHandler {
        @ExceptionHandler(ImageNotFoundException.class)
        final ResponseEntity<ImageErrorResponse> handleNotFoundException(ImageNotFoundException ex,
                        WebRequest request) {
                ImageErrorResponse response = ImageErrorResponse.builder().message(ex.getLocalizedMessage())
                                .timestamp(LocalDateTime.now()).status(HttpStatus.NOT_FOUND).build();

                return new ResponseEntity<>(response, response.getStatus());
        }

        @ExceptionHandler(ConstraintViolationException.class)
        final ResponseEntity<ImageErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                        WebRequest request) {
                List<String> message = ex.getConstraintViolations().stream().map(x -> x.getMessage())
                                .collect(Collectors.toList());

                ImageErrorResponse response = ImageErrorResponse.builder().message(message.toString())
                                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

                return new ResponseEntity<>(response, response.getStatus());
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                        HttpHeaders headers, HttpStatus status, WebRequest request) {
                List<String> message = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
                                .collect(Collectors.toList());

                ImageErrorResponse response = ImageErrorResponse.builder().message(message.toString())
                                .timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST).build();

                return new ResponseEntity<>(response, response.getStatus());
        }

        @ExceptionHandler(Exception.class)
        final ResponseEntity<ImageErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
                ImageErrorResponse response = ImageErrorResponse.builder().message(ex.getLocalizedMessage())
                                .timestamp(LocalDateTime.now()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

                return new ResponseEntity<>(response, response.getStatus());
        }

}
