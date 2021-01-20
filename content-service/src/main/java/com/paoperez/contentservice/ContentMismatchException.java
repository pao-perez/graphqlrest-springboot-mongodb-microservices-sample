package com.paoperez.contentservice;

class ContentMismatchException extends Exception {
  private static final long serialVersionUID = 1L;

  ContentMismatchException(String id, String idArg) {
    super(String.format("Content with id %s does not match content argument %s.", id, idArg));
  }
}
