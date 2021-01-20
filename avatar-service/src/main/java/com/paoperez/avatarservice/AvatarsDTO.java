package com.paoperez.avatarservice;

import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvatarsDTO {
    @NotBlank(message = "data must not be blank")
    private Collection<AvatarDTO> data;
}
