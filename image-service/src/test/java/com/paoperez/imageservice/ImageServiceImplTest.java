package com.paoperez.imageservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

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
        final Image imageA = Image.builder().id("A").name("imageA").url("/path/to/imageA").alt("Image A").width(150)
                .height(150).build();
        final Image imageB = Image.builder().id("B").name("imageB").url("/path/to/imageB").alt("Image B").width(150)
                .height(150).build();
        final List<Image> expected = ImmutableList.of(imageA, imageB);
        when(repository.findAll()).thenReturn(expected);

        Collection<Image> actual = service.getAllImages();

        assertEquals(expected, actual);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getImage_whenExistingId_shouldReturnImage() {
        final String existingId = "A";
        final Image expected = Image.builder().id(existingId).name("imageA").url("/path/to/imageA").alt("Image A")
                .width(150).height(150).build();
        when(repository.findById(existingId)).thenReturn(Optional.ofNullable(expected));

        Image actual = service.getImage(existingId);

        assertEquals(expected, actual);
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    void getImage_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "Z";
        final Optional<Image> nonExistingImage = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingImage);

        Exception actual = assertThrows(ImageNotFoundException.class, () -> service.getImage(nonExistingId));

        String expected = String.format("Image with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    void createImage_whenNonexistingUrl_shouldReturnCreatedImage() {
        final String nonExistingUrl = "/path/to/new/url";
        when(repository.findByUrl(nonExistingUrl)).thenReturn(null);
        final String name = "imageA";
        final String alt = "Image A";
        final Integer width = 150;
        final Integer height = 150;

        final Image newImage = Image.builder().name(name).url(nonExistingUrl).alt(alt).width(width).height(height)
                .build();
        final Image expected = Image.builder().id("A").name(name).url(nonExistingUrl).alt(alt).width(width)
                .height(height).build();
        when(repository.save(newImage)).thenReturn(expected);

        Image actual = service.createImage(newImage);

        assertEquals(expected, actual);
        verify(repository, times(1)).findByUrl(nonExistingUrl);
        verify(repository, times(1)).save(newImage);
    }

    @Test
    void createImage_whenExistingUrl_shouldThrowAlreadyExistsException() {
        final String existingUrl = "/path/to/existing/url";
        final Image existingImage = Image.builder().id("A").name("imageA").url(existingUrl).alt("Image A").width(150)
                .height(150).build();
        when(repository.findByUrl(existingUrl)).thenReturn(existingImage);

        final Image newImage = Image.builder().name("imageB").url(existingUrl).alt("Image B").width(150).height(150)
                .build();
        Exception actual = assertThrows(ImageAlreadyExistsException.class, () -> service.createImage(newImage));

        String expected = String.format("Image with url %s already exists.", existingUrl);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findByUrl(existingUrl);
        verify(repository, times(0)).save(newImage);
    }

    @Test
    void updateImage_whenExistingIdAndNonexistingUrl_shouldNotThrowException() {
        final String existingId = "A";
        final String currentName = "imageA";
        final String currentAlt = "Image A";
        final Integer currentWidth = 150;
        final Integer currentHeight = 150;
        final Optional<Image> existingImage = Optional.of(Image.builder().id(existingId).name(currentName)
                .url("/path/to/old/image").alt(currentAlt).width(currentWidth).height(currentHeight).build());
        when(repository.findById(existingId)).thenReturn(existingImage);
        final String nonExistingUrl = "/path/to/new/image";
        when(repository.findByUrl(nonExistingUrl)).thenReturn(null);

        final Image updateImage = Image.builder().id(existingId).name(currentName).url(nonExistingUrl).alt(currentAlt)
                .width(currentWidth).height(currentHeight).build();
        service.updateImage(existingId, updateImage);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).findByUrl(nonExistingUrl);
        verify(repository, times(1)).save(updateImage);
    }

    @Test
    void updateImage_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "A";
        final Optional<Image> nonExistingImage = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingImage);

        final String updateUrl = "/path/to/new/image";
        final Image updateImage = Image.builder().id(nonExistingId).url(updateUrl).name("imageA").alt("Image A")
                .width(150).height(150).build();
        Exception actual = assertThrows(ImageNotFoundException.class,
                () -> service.updateImage(nonExistingId, updateImage));

        String expected = String.format("Image with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).findByUrl(updateUrl);
        verify(repository, times(0)).save(updateImage);
    }

    @Test
    void updateImage_whenExistingUrl_shouldThrowAlreadyExistsException() {
        final String existingId = "A";
        final String currentName = "imageA";
        final String currentAlt = "Image A";
        final Integer currentWidth = 150;
        final Integer currentHeight = 150;
        final Optional<Image> currentImage = Optional.of(Image.builder().id(existingId).url("/path/to/old/image")
                .name(currentName).alt(currentAlt).width(currentWidth).height(currentHeight).build());
        when(repository.findById(existingId)).thenReturn(currentImage);
        final String existingUrl = "/path/to/new/image";
        final Image existingImage = Image.builder().id("B").url(existingUrl).name("imageB").alt("Image B").width(150)
                .height(150).build();
        when(repository.findByUrl(existingUrl)).thenReturn(existingImage);

        final Image updateImage = Image.builder().id(existingId).url(existingUrl).name(currentName).alt(currentAlt)
                .width(currentWidth).height(currentHeight).build();
        Exception actual = assertThrows(ImageAlreadyExistsException.class,
                () -> service.updateImage(existingId, updateImage));

        String expected = String.format("Image with url %s already exists.", existingUrl);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).findByUrl(existingUrl);
        verify(repository, times(0)).save(updateImage);
    }

    @Test
    void deleteImage_whenExistingId_shouldNotThrowException() {
        final String existingId = "A";
        final Optional<Image> existingImage = Optional.of(Image.builder().id(existingId).url("/path/to/image")
                .name("imageA").alt("Image A").width(150).height(150).build());
        when(repository.findById(existingId)).thenReturn(existingImage);

        service.deleteImage(existingId);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteImage_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "Z";
        final Optional<Image> nonExistingImage = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingImage);

        Exception actual = assertThrows(ImageNotFoundException.class, () -> service.deleteImage(nonExistingId));

        String expected = String.format("Image with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).deleteById(nonExistingId);
    }

}
