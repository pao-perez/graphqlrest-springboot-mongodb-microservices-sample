package com.paoperez.graphql;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import graphql.GraphQL;

@SpringBootTest
public class MainApplicationTests {

  @Autowired GraphQL graphql;

  @Test
  public void contextLoads() {
    assertNotNull(graphql);
  }
}
