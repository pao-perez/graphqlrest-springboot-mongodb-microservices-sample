package com.paoperez.imageservice;

import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Images {
    @NotBlank(message = "data must not be blank")
    private Collection<Image> data;
}
