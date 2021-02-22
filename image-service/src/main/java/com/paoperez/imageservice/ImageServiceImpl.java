package com.paoperez.imageservice;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
final class ImageServiceImpl implements ImageService {
  private final ImageRepository repository;

  ImageServiceImpl(final ImageRepository imageRepository) {
    this.repository = imageRepository;
  }

  public Collection<Image> getAllImages() {
    return repository.findAll();
  }

  public Image getImage(final String id) throws ImageNotFoundException {
    return repository.findById(id).orElseThrow(() -> new ImageNotFoundException(id));
  }

  public String createImage(final Image image) throws ImageAlreadyExistsException {
    final String url = image.getUrl();

    if (repository.findByUrl(url) != null) {
      throw new ImageAlreadyExistsException(url);
    }

    return repository.save(image).getId();
  }

  public void updateImage(final String id, final Image image)
      throws ImageNotFoundException, ImageAlreadyExistsException, ImageMismatchException {
    Optional<Image> retrievedImage = repository.findById(id);
    if (!retrievedImage.isPresent()) {
      throw new ImageNotFoundException(id);
    }

    String imageId = image.getId();
    if (!id.equals(imageId)) {
      throw new ImageMismatchException(id, imageId);
    }

    String imageUrl = image.getUrl();
    Image imageFromUrl = repository.findByUrl(imageUrl);
    if (imageFromUrl != null && !imageFromUrl.getId().equals(id)) {
      throw new ImageAlreadyExistsException(imageUrl);
    }

    repository.save(image);
  }

  public void deleteImage(final String id) throws ImageNotFoundException {
    if (!repository.findById(id).isPresent()) {
      throw new ImageNotFoundException(id);
    }

    repository.deleteById(id);
  }
}
