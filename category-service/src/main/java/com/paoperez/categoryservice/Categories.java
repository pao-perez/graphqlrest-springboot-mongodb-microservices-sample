package com.paoperez.categoryservice;

import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Categories {
    @NotBlank(message = "data must not be blank")
    private Collection<Category> data;
}
