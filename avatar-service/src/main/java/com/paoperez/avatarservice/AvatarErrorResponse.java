package com.paoperez.avatarservice;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
class AvatarErrorResponse {
  private String message;
  private LocalDateTime timestamp;
  private HttpStatus status;
}
