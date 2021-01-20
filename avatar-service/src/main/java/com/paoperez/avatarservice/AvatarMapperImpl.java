package com.paoperez.avatarservice;

import java.util.Collection;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

final class AvatarMapperImpl implements AvatarMapper {
    private final ModelMapper mapper;

    AvatarMapperImpl(final ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Avatar avatarDtoToAvatar(AvatarDTO avatarDto) {
        return mapper.map(avatarDto, Avatar.class);
    }

    @Override
    public AvatarDTO avatarToAvatarDto(Avatar avatar) {
        return mapper.map(avatar, AvatarDTO.class);
    }

    @Override
    public Collection<AvatarDTO> avatarsToAvatarsDTO(Collection<Avatar> avatars) {
        return mapCollection(avatars, AvatarDTO.class);
    }

    private <S, T> Collection<T> mapCollection(Collection<S> source, Class<T> targetClass) {
        return source.stream().map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
