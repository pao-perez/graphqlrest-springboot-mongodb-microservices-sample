package com.paoperez.imageservice;

import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
final class ImageServiceImpl implements ImageService {
  private final ImageRepository imageRepository;

  ImageServiceImpl(final ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  public Collection<Image> getAllImages() {
    return imageRepository.findAll();
  }

  public Image getImage(final String id) throws ImageNotFoundException {
    return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException(id));
  }

  public Image createImage(final Image image) throws ImageAlreadyExistsException {
    final String url = image.getUrl();

    if (imageRepository.findByUrl(url) != null) {
      throw new ImageAlreadyExistsException(url);
    }
    
    return imageRepository.save(image);
  }

  public void updateImage(final String id, final Image image)
      throws ImageNotFoundException, ImageAlreadyExistsException {
    imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException(id));

    final String imageUrl = image.getUrl();
    final Image currentImage = imageRepository.findByUrl(imageUrl);
    if (currentImage != null && !currentImage.getId().equals(id)) {
      throw new ImageAlreadyExistsException(imageUrl);
    }

    Image updateImage =
        Image.builder()
            .url(imageUrl)
            .name(image.getName())
            .alt(image.getAlt())
            .height(image.getHeight())
            .width(image.getWidth())
            .id(id)
            .build();
    imageRepository.save(updateImage);
  }

  public void deleteImage(final String id) throws ImageNotFoundException {
    imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException(id));
    imageRepository.deleteById(id);
  }
}
