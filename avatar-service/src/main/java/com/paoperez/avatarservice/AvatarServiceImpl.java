package com.paoperez.avatarservice;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
final class AvatarServiceImpl implements AvatarService {
  private final AvatarRepository repository;

  AvatarServiceImpl(final AvatarRepository avatarRepository) {
    this.repository = avatarRepository;
  }

  public Collection<Avatar> getAllAvatars() {
    return repository.findAll();
  }

  public Avatar getAvatar(final String id) throws AvatarNotFoundException {
    return repository.findById(id).orElseThrow(() -> new AvatarNotFoundException(id));
  }

  public String createAvatar(final Avatar avatar) throws AvatarAlreadyExistsException {
    String userName = avatar.getUserName();

    if (repository.findByUserName(userName) != null) {
      throw new AvatarAlreadyExistsException(userName);
    }

    return repository.save(avatar).getId();
  }

  public void updateAvatar(final String id, final Avatar avatar)
      throws AvatarNotFoundException, AvatarAlreadyExistsException, AvatarMismatchException {
    Optional<Avatar> retrievedAvatar = repository.findById(id);
    if (retrievedAvatar.isEmpty()) {
      throw new AvatarNotFoundException(id);
    }
    String avatarId = avatar.getId();
    if (!id.equals(avatarId)) {
      throw new AvatarMismatchException(id, avatarId);
    }
    String avatarUserName = avatar.getUserName();
    Avatar avatarFromUserName = repository.findByUserName(avatarUserName);
    if (avatarFromUserName != null && !avatarFromUserName.getId().equals(id)) {
      throw new AvatarAlreadyExistsException(avatarUserName);
    }
    repository.save(avatar);
  }

  public void deleteAvatar(final String id) throws AvatarNotFoundException {
    if (repository.findById(id).isEmpty()) {
      throw new AvatarNotFoundException(id);
    }
    repository.deleteById(id);
  }
}
