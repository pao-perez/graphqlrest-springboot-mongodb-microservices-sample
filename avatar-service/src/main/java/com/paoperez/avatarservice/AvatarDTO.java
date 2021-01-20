package com.paoperez.avatarservice;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AvatarDTO {
    private String id;

    @NotBlank(message = "userName must not be blank")
    private String userName;

    @NotBlank(message = "imageId must not be blank")
    private String imageId;
}
