package com.paoperez.categoryservice;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private String id;

    @NotBlank(message = "name must not be blank")
    private String name;
}
