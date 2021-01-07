package com.paoperez.contentservice;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDTO {
    private String id;
    private ZonedDateTime created;
    private ZonedDateTime updated;

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "body must not be blank")
    private String body;

    @NotNull(message = "rank must not be empty")
    @Positive(message = "rank must be a positive number")
    private Integer rank;

    @NotBlank(message = "categoryId must not be blank")
    private String categoryId;

    @NotBlank(message = "avatarId must not be blank")
    private String avatarId;

    @NotBlank(message = "imageId must not be blank")
    private String imageId;
}
