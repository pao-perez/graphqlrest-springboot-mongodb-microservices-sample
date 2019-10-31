package com.paoperez.imageservice;

import java.util.Collection;

interface ImageService {
    Collection<Image> getAllImages();

    Image getImage(String id) throws ImageNotFoundException;

    Image createImage(Image image) throws ImageAlreadyExistsException;

    void updateImage(String id, Image image) throws ImageNotFoundException;

    void deleteImage(String id) throws ImageNotFoundException;
}
