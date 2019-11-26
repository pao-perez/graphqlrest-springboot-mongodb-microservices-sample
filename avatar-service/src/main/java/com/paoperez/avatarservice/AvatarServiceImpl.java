package com.paoperez.avatarservice;

import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
final class AvatarServiceImpl implements AvatarService {
  private final AvatarRepository avatarRepository;

  AvatarServiceImpl(final AvatarRepository avatarRepository) {
    this.avatarRepository = avatarRepository;
  }

  public Collection<Avatar> getAllAvatars() {
    return avatarRepository.findAll();
  }

  public Avatar getAvatar(final String id) throws AvatarNotFoundException {
    return avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
  }

  public Avatar createAvatar(final Avatar avatar) throws AvatarAlreadyExistsException {
    final String userName = avatar.getUserName();

    if (avatarRepository.findByUserName(userName) != null) {
      throw new AvatarAlreadyExistsException(userName);
    }

    return avatarRepository.save(avatar);
  }

  public void updateAvatar(final String id, final Avatar avatar)
      throws AvatarNotFoundException, AvatarAlreadyExistsException {
    avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));

    final String avatarUserName = avatar.getUserName();
    final Avatar currentAvatar = avatarRepository.findByUserName(avatarUserName);
    if (currentAvatar != null && !currentAvatar.getId().equals(id)) {
      throw new AvatarAlreadyExistsException(avatarUserName);
    }

    Avatar updateAvatar =
        Avatar.builder().userName(avatarUserName).imageId(avatar.getImageId()).id(id).build();
    avatarRepository.save(updateAvatar);
  }

  public void deleteAvatar(final String id) throws AvatarNotFoundException {
    avatarRepository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
    avatarRepository.deleteById(id);
  }
}
