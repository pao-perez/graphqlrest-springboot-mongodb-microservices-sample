package com.paoperez.avatarservice;

import java.util.Collection;

interface AvatarService {

  /**
   * 
   * @return Collection<Avatar> - a collection of all avatars.
   */
  Collection<Avatar> getAllAvatars();

  /**
   * 
   * @param id - The id of the avatar to be retrieved
   * @return Avatar - The avatar to be retrieved
   * @throws AvatarNotFoundException - Thrown when the id of the avatar to be retrieved was not
   *                                 found.
   */
  Avatar getAvatar(String id) throws AvatarNotFoundException;

  /**
   * 
   * @param avatar - The avatar to be created.
   * @return String - The ID of the created avatar.
   * @throws AvatarAlreadyExistsException - Thrown when the username of the avatar in the argument
   *                                      already exists.
   */
  String createAvatar(Avatar avatar) throws AvatarAlreadyExistsException;

  /**
   * 
   * @param id     - The id of the avatar to be updated
   * @param avatar - The avatar to be updated.
   * @throws AvatarNotFoundException      - Thrown when the id of the avatar to be updated was not
   *                                      found.
   * @throws AvatarAlreadyExistsException - Thrown when the username of the avatar in the argument
   *                                      already exists.
   * @throws AvatarMismatchException      - Thrown when the id in the argument did not match the id
   *                                      in the avatar argument.
   */
  void updateAvatar(String id, Avatar avatar)
      throws AvatarNotFoundException, AvatarAlreadyExistsException, AvatarMismatchException;

  /**
   * 
   * @param id - The id of the avatar to be deleted
   * @throws AvatarNotFoundException - Thrown when the id of the avatar to be deleted was not found.
   */
  void deleteAvatar(String id) throws AvatarNotFoundException;
}
