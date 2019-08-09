package com.paoperez.avatarservice;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
class Avatar {
    @Id
    private String id;
    @NotBlank
    private String userName;
    @NotBlank
    private String imageId;
}
