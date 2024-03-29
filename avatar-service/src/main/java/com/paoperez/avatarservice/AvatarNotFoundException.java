package com.paoperez.avatarservice;

class AvatarNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  AvatarNotFoundException(final String id) {
    super(String.format("Avatar with id %s not found.", id));
  }
}
