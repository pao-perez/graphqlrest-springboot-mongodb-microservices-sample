package com.paoperez.categoryservice;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;

import lombok.Value;

@Value
class Category {
    @Id
    private String id;
    @NotBlank(message = "name must not be empty")
    private String name;
}
