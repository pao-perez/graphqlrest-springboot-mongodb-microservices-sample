package com.paoperez.contentservice;

class ContentNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  ContentNotFoundException(String id) {
    super(String.format("Content with id %s not found.", id));
  }
}
