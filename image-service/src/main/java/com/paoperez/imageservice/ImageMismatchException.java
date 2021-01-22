package com.paoperez.imageservice;

class ImageMismatchException extends Exception {
    private static final long serialVersionUID = 1L;

    ImageMismatchException(String id, String imageId) {
        super(String.format("Image with id %s does not match image argument %s.", id, imageId));
    }
}
