package com.paoperez.categoryservice;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
class CategoryErrorResponse {
  private String message;
  private LocalDateTime timestamp;
  private HttpStatus status;
}
