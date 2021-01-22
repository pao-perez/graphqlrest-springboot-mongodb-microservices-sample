package com.paoperez.imageservice;

import java.net.URI;
import java.util.Collection;
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
public class ImageController {
  private final ImageService imageService;
  private final ImageMapper imageMapper;

  public ImageController(final ImageService imageService, ImageMapper imageMapper) {
    this.imageService = imageService;
    this.imageMapper = imageMapper;
  }

  @GetMapping()
  public ResponseEntity<ImagesDTO> getAllImages() {
    Collection<ImageDTO> images = imageMapper.imagesToImageDTOs(imageService.getAllImages());
    ImagesDTO imagesDTO = ImagesDTO.builder().data(images).build();
    return new ResponseEntity<>(imagesDTO, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ImageDTO> getImage(final @PathVariable @NotBlank String id)
      throws ImageNotFoundException {
    ImageDTO imageDTO = imageMapper.imageToImageDto(imageService.getImage(id));
    return new ResponseEntity<>(imageDTO, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> createImage(final @RequestBody @Valid ImageDTO imageRequest)
      throws ImageAlreadyExistsException {
    Image image = imageMapper.imageDtoToImage(imageRequest);
    String id = imageService.createImage(image);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);

    return new ResponseEntity<>(id, headers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateImage(final @PathVariable @NotBlank String id,
      final @RequestBody @Valid ImageDTO imageRequest)
      throws ImageNotFoundException, ImageAlreadyExistsException, ImageMismatchException {
    Image image = imageMapper.imageDtoToImage(imageRequest);
    imageService.updateImage(id, image);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteImage(final @PathVariable @NotBlank String id)
      throws ImageNotFoundException {
    imageService.deleteImage(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
