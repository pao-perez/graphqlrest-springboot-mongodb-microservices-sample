package com.paoperez.avatarservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class AvatarService {
    @Autowired
    private AvatarRepository avatarRepository;

    List<Avatar> getAllAvatars() {
        return avatarRepository.findAll();
    }

    Avatar getAvatar(String id) {
        return avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
    }

    Avatar createAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    Avatar updateAvatar(Avatar avatar) {
        if (!avatarRepository.existsById(avatar.getId()))
            throw new AvatarNotFoundException(avatar.getId());
        return avatarRepository.save(avatar);
    }

    Boolean deleteAvatar(String id) {
        if (!avatarRepository.existsById(id))
            throw new AvatarNotFoundException(id);
        avatarRepository.deleteById(id);
        return true;
    }

}
