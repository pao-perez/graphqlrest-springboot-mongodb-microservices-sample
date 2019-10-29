package com.paoperez.avatarservice;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Avatar {
    @Id
    private String id;
    @NotBlank(message = "userName must not be blank")
    private String userName;
    private String imageId;
}
