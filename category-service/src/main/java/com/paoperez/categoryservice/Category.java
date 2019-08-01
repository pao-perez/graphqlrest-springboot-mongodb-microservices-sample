package com.paoperez.categoryservice;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Category {
    @Id
    private String id;
    private String name;
}
