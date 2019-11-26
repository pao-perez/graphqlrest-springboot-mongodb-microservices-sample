package com.paoperez.categoryservice;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
class CategoryErrorResponse {
  private String message;
  private LocalDateTime timestamp;
  private HttpStatus status;
}
