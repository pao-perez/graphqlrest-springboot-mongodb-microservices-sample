package com.paoperez.avatarservice;

class AvatarNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    AvatarNotFoundException(String id) {
        super(String.format("Avatar with id %s not found.", id));
    }
}
