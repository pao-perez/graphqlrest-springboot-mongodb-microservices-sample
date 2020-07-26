package com.paoperez.categoryservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {
  private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

  public static void main(String[] args) {
    logger.info("Starting Category service...");
    SpringApplication.run(MainApplication.class, args);
  }
}