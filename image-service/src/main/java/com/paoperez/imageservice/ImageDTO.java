package com.paoperez.imageservice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
class ImageDTO {
    private String id;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "url must not be blank")
    private String url;

    @NotBlank(message = "alt must not be blank")
    private String alt;

    @NotNull(message = "width must not be blank")
    @Positive(message = "width must be a positive number")
    private Integer width;

    @NotNull(message = "height must not be blank")
    @Positive(message = "height must be a positive number")
    private Integer height;
}
