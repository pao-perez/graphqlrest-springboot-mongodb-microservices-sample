package com.paoperez.imageservice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Image {
    @Id
    private String id;
    @NotBlank(message = "name must not be blank")
    private String name;
    @NotBlank(message = "url must not be blank")
    private String url;
    @NotBlank(message = "alt must not be blank")
    private String alt;
    @NotNull
    @Positive(message = "width must be a positive number")
    private Integer width;
    @NotNull
    @Positive(message = "heigth must be a positive number")
    private Integer height;
}
