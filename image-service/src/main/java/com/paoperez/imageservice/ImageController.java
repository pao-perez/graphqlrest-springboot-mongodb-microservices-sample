package com.paoperez.imageservice;

import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping("/images")
class ImageController {
  private final ImageService imageService;

  ImageController(final ImageService imageService) {
    this.imageService = imageService;
  }

  @GetMapping()
  ResponseEntity<Images> getAllImages() {
    Images images = Images.builder().data(imageService.getAllImages()).build();
    return new ResponseEntity<>(images, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  ResponseEntity<Image> getImage(final @PathVariable @NotBlank String id)
      throws ImageNotFoundException {
    return new ResponseEntity<>(imageService.getImage(id), HttpStatus.OK);
  }

  @PostMapping()
  ResponseEntity<Image> createImage(final @RequestBody @Valid Image image)
      throws ImageAlreadyExistsException {
    Image createdImage = imageService.createImage(image);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdImage.getId()).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);

    return new ResponseEntity<>(createdImage, headers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  ResponseEntity<Void> updateImage(final @PathVariable @NotBlank String id,
      final @RequestBody @Valid Image image)
      throws ImageNotFoundException, ImageAlreadyExistsException {
    imageService.updateImage(id, image);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteImage(final @PathVariable @NotBlank String id)
      throws ImageNotFoundException {
    imageService.deleteImage(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
