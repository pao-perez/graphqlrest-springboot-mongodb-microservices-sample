package com.paoperez.contentservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainApplicationTest {

    @Autowired
    private ContentController controller;

    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

}
