package com.paoperez.categoryservice;

import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Category {
    @Id
    private String id;
    @NotBlank(message = "name must not be blank")
    private String name;
}
