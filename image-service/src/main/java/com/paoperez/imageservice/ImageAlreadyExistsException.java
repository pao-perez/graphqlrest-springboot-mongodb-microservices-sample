package com.paoperez.imageservice;

class ImageAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ImageAlreadyExistsException(final String url) {
        super(String.format("Image with url %s already exists.", url));
    }
}
