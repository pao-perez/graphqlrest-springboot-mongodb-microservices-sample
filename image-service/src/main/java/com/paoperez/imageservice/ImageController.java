package com.paoperez.imageservice;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
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

@Validated
@RestController
@RequestMapping("/images")
class ImageController {
    @Autowired
    private ImageService imageService;

    @GetMapping()
    ResponseEntity<List<Image>> getAllImages() {
        return new ResponseEntity<>(imageService.getAllImages(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<Image> getImage(@PathVariable @NotBlank String id) {
        return new ResponseEntity<>(imageService.getImage(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    ResponseEntity<Image> createImage(@RequestBody @Valid Image image) {
        return new ResponseEntity<>(imageService.createImage(image), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    ResponseEntity<Image> updateImage(@RequestBody @Valid Image image) {
        return new ResponseEntity<>(imageService.updateImage(image), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Boolean> deleteImage(@PathVariable @NotBlank String id) {
        return new ResponseEntity<>(imageService.deleteImage(id), HttpStatus.OK);
    }

}
