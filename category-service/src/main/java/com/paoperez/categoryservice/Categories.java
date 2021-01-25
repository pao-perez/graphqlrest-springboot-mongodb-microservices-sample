package com.paoperez.categoryservice;

import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class CategoriesDTO {
    @NotBlank(message = "data must not be blank")
    private Collection<CategoryDTO> data;
}
