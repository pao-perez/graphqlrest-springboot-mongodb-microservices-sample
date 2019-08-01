package com.paoperez.avatarservice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvatarService {
    @Autowired
    private AvatarRepository avatarRepository;

    public List<Avatar> getAllAvatars() {
        return avatarRepository.findAll();
    }

    public Optional<Avatar> getAvatar(String id) {
        return avatarRepository.findById(id);
    }

    public Avatar createAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    public Avatar updateAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    public void deleteAvatar(String id) {
        avatarRepository.deleteById(id);
    }

}
