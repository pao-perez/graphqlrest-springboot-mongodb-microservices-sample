package com.paoperez.imageservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainApplicationTest {

  @Autowired private ImageController controller;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
  }
}
