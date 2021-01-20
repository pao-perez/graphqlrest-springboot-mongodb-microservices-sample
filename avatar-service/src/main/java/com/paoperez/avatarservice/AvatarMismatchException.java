package com.paoperez.avatarservice;

class AvatarMismatchException extends Exception {
    private static final long serialVersionUID = 1L;

    AvatarMismatchException(String id, String avatarId) {
        super(String.format("Avatar with id %s does not match avatar argument %s.", id, avatarId));
    }
}
