package com.paoperez.contentservice;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
class Content {
    @Id
    private String id;
    @NotBlank
    private String created;
    private String updated;
    @NotBlank
    private String title;
    @NotBlank
    private String body;
    @NotNull
    @Positive
    private Integer rank;
    @NotBlank
    private String categoryId;
    @NotBlank
    private String avatarId;
    @NotBlank
    private String imageId;
}
