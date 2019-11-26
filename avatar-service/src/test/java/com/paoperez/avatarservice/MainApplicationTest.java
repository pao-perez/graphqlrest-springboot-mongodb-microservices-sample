package com.paoperez.avatarservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainApplicationTest {

  @Autowired private AvatarController controller;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
  }
}
