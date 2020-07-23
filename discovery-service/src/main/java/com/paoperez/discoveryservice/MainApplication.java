package com.paoperez.discoveryservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MainApplication {
  private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

  public static void main(String[] args) {
    logger.info("Starting Discovery service...");
    SpringApplication.run(MainApplication.class, args);
  }
}