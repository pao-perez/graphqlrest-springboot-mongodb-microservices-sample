package com.paoperez.imageservice;

import java.util.Collection;

public interface ImageMapper {
    Image imageDtoToImage(ImageDTO imageDto);

    ImageDTO imageToImageDto(Image image);

    Collection<ImageDTO> imagesToImageDTOs(Collection<Image> images);
}
