package com.paoperez.imageservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    Image getImage(String id) {
        return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException(id));
    }

    Image createImage(Image image) {
        return imageRepository.save(image);
    }

    Image updateImage(Image image) {
        if (!imageRepository.existsById(image.getId()))
            throw new ImageNotFoundException(image.getId());
        return imageRepository.save(image);
    }

    Boolean deleteImage(String id) {
        if (!imageRepository.existsById(id))
            throw new ImageNotFoundException(id);
        imageRepository.deleteById(id);
        return true;
    }

}
