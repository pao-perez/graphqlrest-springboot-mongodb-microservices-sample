package com.paoperez.avatarservice;

class AvatarAlreadyExistsException extends Exception {
  private static final long serialVersionUID = 1L;

  AvatarAlreadyExistsException(final String userName) {
    super(String.format("Avatar with userName %s already exists.", userName));
  }
}
