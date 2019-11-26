package com.paoperez.categoryservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainApplicationTest {

  @Autowired
  private CategoryController controller;

  @Test
  void contextLoads() {
    assertNotNull(controller);
  }
}
