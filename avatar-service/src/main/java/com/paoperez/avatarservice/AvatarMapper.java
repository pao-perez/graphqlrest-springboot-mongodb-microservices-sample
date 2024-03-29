package com.paoperez.avatarservice;

import java.util.Collection;

interface AvatarMapper {
    Avatar avatarDtoToAvatar(AvatarDTO avatarDto);

    AvatarDTO avatarToAvatarDto(Avatar avatar);

    Collection<AvatarDTO> avatarsToAvatarDTOs(Collection<Avatar> avatars);
}
