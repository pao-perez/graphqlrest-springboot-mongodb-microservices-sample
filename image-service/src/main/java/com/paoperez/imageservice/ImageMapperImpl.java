package com.paoperez.imageservice;

import java.util.Collection;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

final class ImageMapperImpl implements ImageMapper {
    private final ModelMapper mapper;

    ImageMapperImpl(final ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Image imageDtoToImage(ImageDTO imageDto) {
        return mapper.map(imageDto, Image.class);
    }

    @Override
    public ImageDTO imageToImageDto(Image image) {
        return mapper.map(image, ImageDTO.class);
    }

    @Override
    public Collection<ImageDTO> imagesToImageDTOs(Collection<Image> images) {
        return mapCollection(images, ImageDTO.class);
    }

    private <S, T> Collection<T> mapCollection(Collection<S> source, Class<T> targetClass) {
        return source.stream().map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
