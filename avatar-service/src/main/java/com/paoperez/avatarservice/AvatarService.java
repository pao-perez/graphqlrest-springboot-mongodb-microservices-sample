package com.paoperez.avatarservice;

import java.util.Collection;

interface AvatarService {
    Collection<Avatar> getAllAvatars();

    Avatar getAvatar(String id) throws AvatarNotFoundException;

    Avatar createAvatar(Avatar avatar) throws AvatarAlreadyExistsException;

    void updateAvatar(String id, Avatar avatar) throws AvatarNotFoundException, AvatarAlreadyExistsException;

    void deleteAvatar(String id) throws AvatarNotFoundException;
}
