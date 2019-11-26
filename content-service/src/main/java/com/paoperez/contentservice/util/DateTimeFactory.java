package com.paoperez.contentservice.util;

import java.time.LocalDateTime;

public class DateTimeFactory {
  private static DateTimeFactory instance = null;

  private DateTimeFactory() {}

  public static DateTimeFactory instance() {
    if (instance == null) {
      instance = new DateTimeFactory();
    }

    return instance;
  }

  public String dateTime() {
    return LocalDateTime.now().toString();
  }
}
