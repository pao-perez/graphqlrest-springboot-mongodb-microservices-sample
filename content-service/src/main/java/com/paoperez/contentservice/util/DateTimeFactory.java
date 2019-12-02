package com.paoperez.contentservice.util;

import java.time.LocalDateTime;

public class DateTimeFactory {
  public String dateTime() {
    return LocalDateTime.now().toString();
  }
}
