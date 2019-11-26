package com.paoperez.imageservice;

class ImageNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  ImageNotFoundException(String id) {
    super(String.format("Image with id %s not found.", id));
  }
}
