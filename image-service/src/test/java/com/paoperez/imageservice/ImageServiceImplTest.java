package com.paoperez.imageservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ImageServiceImplTest {
  private ImageService service;

  @MockBean
  private ImageRepository repository;

  @BeforeEach
  void init() {
    service = new ImageServiceImpl(repository);
  }

  @Test
  void getAllImages_shouldReturnImages() {
    Image imageA = new Image();
    imageA.setName("imageA");
    imageA.setUrl("/path/to/imageA");
    imageA.setAlt("image A");
    imageA.setWidth(150);
    imageA.setHeight(150);
    imageA.setId("A");
    Image imageB = new Image();
    imageB.setName("imageB");
    imageB.setUrl("/path/to/imageB");
    imageB.setAlt("image B");
    imageB.setWidth(150);
    imageB.setHeight(150);
    imageB.setId("B");
    List<Image> expected = ImmutableList.of(imageA, imageB);
    when(repository.findAll()).thenReturn(expected);

    Collection<Image> actual = service.getAllImages();

    assertEquals(expected, actual);
    verify(repository, times(1)).findAll();
  }

  @Test
  void getImage_whenExistingId_shouldReturnImage() throws ImageNotFoundException {
    String existingId = "A";
    Image expected = new Image();
    expected.setName("imageA");
    expected.setUrl("/path/to/imageA");
    expected.setAlt("image A");
    expected.setWidth(150);
    expected.setHeight(150);
    expected.setId("A");
    when(repository.findById(existingId)).thenReturn(Optional.of(expected));

    Image actual = service.getImage(existingId);

    assertEquals(expected, actual);
    verify(repository, times(1)).findById(existingId);
  }

  @Test
  void getImage_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Image> nonExistingImage = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingImage);

    Exception actual =
        assertThrows(ImageNotFoundException.class, () -> service.getImage(nonExistingId));

    String expected = String.format("Image with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
  }

  @Test
  void createImage_whenNonexistingUrl_shouldReturnCreatedImage()
      throws ImageAlreadyExistsException {
    String nonExistingUrl = "/path/to/new/url";
    when(repository.findByUrl(nonExistingUrl)).thenReturn(null);
    Image image = new Image();
    image.setName("imageA");
    image.setUrl(nonExistingUrl);
    image.setAlt("image A");
    image.setWidth(150);
    image.setHeight(150);
    Image expected = new Image();
    expected.setName("imageA");
    expected.setUrl(nonExistingUrl);
    expected.setAlt("image A");
    expected.setWidth(150);
    expected.setHeight(150);
    expected.setId("A");
    when(repository.save(image)).thenReturn(expected);

    String actual = service.createImage(image);

    assertEquals(expected.getId(), actual);
    verify(repository, times(1)).findByUrl(nonExistingUrl);
    verify(repository, times(1)).save(image);
  }

  @Test
  void createImage_whenExistingUrl_shouldThrowAlreadyExistsException() {
    String existingUrl = "/path/to/existing/url";
    Image existingImage = new Image();
    existingImage.setName("imageB");
    existingImage.setUrl(existingUrl);
    existingImage.setAlt("image B");
    existingImage.setWidth(100);
    existingImage.setHeight(100);
    existingImage.setId("B");
    when(repository.findByUrl(existingUrl)).thenReturn(existingImage);
    Image image = new Image();
    image.setName("imageA");
    image.setUrl(existingUrl);
    image.setAlt("image A");
    image.setWidth(150);
    image.setHeight(150);

    Exception actual =
        assertThrows(ImageAlreadyExistsException.class, () -> service.createImage(image));

    String expected = String.format("Image with url %s already exists.", existingUrl);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findByUrl(existingUrl);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateImage_whenExistingIdAndNonexistingUrl_shouldNotThrowException()
      throws ImageNotFoundException, ImageAlreadyExistsException, ImageMismatchException {
    String existingId = "A";
    Image retrievedImage = new Image();
    retrievedImage.setName("imageA");
    retrievedImage.setUrl("/path/to/old/image");
    retrievedImage.setAlt("image A");
    retrievedImage.setWidth(150);
    retrievedImage.setHeight(150);
    retrievedImage.setId(existingId);
    when(repository.findById(existingId)).thenReturn(Optional.of(retrievedImage));
    String nonExistingUrl = "/path/to/new/image";
    when(repository.findByUrl(nonExistingUrl)).thenReturn(null);
    Image updateImage = new Image();
    updateImage.setName("imageA");
    updateImage.setUrl(nonExistingUrl);
    updateImage.setAlt("image A");
    updateImage.setWidth(150);
    updateImage.setHeight(150);
    updateImage.setId(existingId);

    service.updateImage(existingId, updateImage);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).findByUrl(nonExistingUrl);
    verify(repository, times(1)).save(updateImage);
  }

  @Test
  void updateImage_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "A";
    Optional<Image> nonExistingImage = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingImage);
    Image updateImage = new Image();
    updateImage.setName("imageA");
    updateImage.setUrl("/path/to/A");
    updateImage.setAlt("image A");
    updateImage.setWidth(150);
    updateImage.setHeight(150);
    updateImage.setId(nonExistingId);

    Exception actual = assertThrows(ImageNotFoundException.class,
        () -> service.updateImage(nonExistingId, updateImage));

    String expected = String.format("Image with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).findByUrl(null);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateImage_whenExistingUrl_shouldThrowAlreadyExistsException() {
    String id = "A";
    Image retrievedImage = new Image();
    retrievedImage.setName("imageA");
    retrievedImage.setUrl("/path/to/old");
    retrievedImage.setAlt("image A");
    retrievedImage.setWidth(150);
    retrievedImage.setHeight(150);
    retrievedImage.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(retrievedImage));
    String existingUrl = "/path/to/existing/url";
    Image differentImage = new Image();
    differentImage.setName("imageB");
    differentImage.setUrl(existingUrl);
    differentImage.setAlt("image B");
    differentImage.setWidth(100);
    differentImage.setHeight(100);
    differentImage.setId("differentId");
    when(repository.findByUrl(existingUrl)).thenReturn(differentImage);
    Image updateImage = new Image();
    updateImage.setName("imageA");
    updateImage.setUrl(existingUrl);
    updateImage.setAlt("image A");
    updateImage.setWidth(150);
    updateImage.setHeight(150);
    updateImage.setId(id);

    Exception actual =
        assertThrows(ImageAlreadyExistsException.class, () -> service.updateImage(id, updateImage));

    String expected = String.format("Image with url %s already exists.", existingUrl);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(1)).findByUrl(existingUrl);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateImage_whenMismatchId_shouldThrowMismatchException() {
    String id = "A";
    Image retrievedImage = new Image();
    retrievedImage.setName("imageA");
    retrievedImage.setUrl("/path/to/A");
    retrievedImage.setAlt("image A");
    retrievedImage.setWidth(150);
    retrievedImage.setHeight(150);
    retrievedImage.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(retrievedImage));
    String differentId = "differentId";
    Image differentImage = new Image();
    differentImage.setName("imageB");
    differentImage.setUrl("/path/to/B");
    differentImage.setAlt("image B");
    differentImage.setWidth(100);
    differentImage.setHeight(100);
    differentImage.setId(differentId);

    Exception actual =
        assertThrows(ImageMismatchException.class, () -> service.updateImage(id, differentImage));

    String expected =
        String.format("Image with id %s does not match image argument %s.", id, differentId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(0)).findByUrl(null);
    verify(repository, times(0)).save(null);
  }

  @Test
  void deleteImage_whenExistingId_shouldNotThrowException() throws ImageNotFoundException {
    String existingId = "A";
    Image existingImage = new Image();
    existingImage.setName("imageA");
    existingImage.setUrl("/path/to/A");
    existingImage.setAlt("image A");
    existingImage.setWidth(150);
    existingImage.setHeight(150);
    existingImage.setId(existingId);
    when(repository.findById(existingId)).thenReturn(Optional.of(existingImage));

    service.deleteImage(existingId);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).deleteById(existingId);
  }

  @Test
  void deleteImage_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Image> nonExistingImage = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingImage);

    Exception actual =
        assertThrows(ImageNotFoundException.class, () -> service.deleteImage(nonExistingId));

    String expected = String.format("Image with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).deleteById(null);
  }
}
