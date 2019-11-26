package com.paoperez.avatarservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AvatarServiceImplTest {
  private AvatarService service;

  @MockBean
  private AvatarRepository repository;

  @BeforeEach
  void init() {
    service = new AvatarServiceImpl(repository);
  }

  @Test
  void getAllAvatars_shouldReturnAvatars() {
    final Avatar AvatarA = Avatar.builder().id("A").userName("userA").imageId("imageIdA").build();
    final Avatar AvatarB = Avatar.builder().id("B").userName("userB").build();
    final List<Avatar> expected = ImmutableList.of(AvatarA, AvatarB);
    when(repository.findAll()).thenReturn(expected);

    Collection<Avatar> actual = service.getAllAvatars();

    assertEquals(expected, actual);
    verify(repository, times(1)).findAll();
  }

  @Test
  void getAvatar_whenExistingId_shouldReturnAvatar() throws Exception {
    final String existingId = "A";
    final Avatar expected = Avatar.builder()
        .id(existingId)
        .userName("userA")
        .imageId("imageIdA")
        .build();
    when(repository.findById(existingId)).thenReturn(Optional.ofNullable(expected));

    Avatar actual = service.getAvatar(existingId);

    assertEquals(expected, actual);
    verify(repository, times(1)).findById(existingId);
  }

  @Test
  void getAvatar_whenNonexistingId_shouldThrowNotFoundException() {
    final String nonExistingId = "Z";
    final Optional<Avatar> nonExistingAvatar = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingAvatar);

    Exception actual = assertThrows(AvatarNotFoundException.class, 
        () -> service.getAvatar(nonExistingId));

    String expected = String.format("Avatar with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
  }

  @Test
  void createAvatar_whenNonexistingUserName_shouldReturnCreatedAvatar() throws Exception {
    final String nonExistingUserName = "userA";
    when(repository.findByUserName(nonExistingUserName)).thenReturn(null);
    final Avatar newAvatar = Avatar.builder().userName(nonExistingUserName).build();
    final Avatar expected = Avatar.builder().id("A").userName(nonExistingUserName).build();
    when(repository.save(newAvatar)).thenReturn(expected);

    Avatar actual = service.createAvatar(newAvatar);

    assertEquals(expected, actual);
    verify(repository, times(1)).findByUserName(nonExistingUserName);
    verify(repository, times(1)).save(newAvatar);
  }

  @Test
  void createAvatar_whenExistingUserName_shouldThrowAlreadyExistsException() {
    final String existingUserName = "userA";
    final Avatar existingAvatar = Avatar.builder().id("A").userName(existingUserName).build();
    when(repository.findByUserName(existingUserName)).thenReturn(existingAvatar);

    final Avatar newAvatar = Avatar.builder().userName(existingUserName).build();
    Exception actual = assertThrows(AvatarAlreadyExistsException.class, 
        () -> service.createAvatar(newAvatar));

    String expected = String.format("Avatar with userName %s already exists.", existingUserName);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findByUserName(existingUserName);
    verify(repository, times(0)).save(newAvatar);
  }

  @Test
  void updateAvatar_whenExistingIdAndNonexistingUserName_shouldNotThrowException()
      throws AvatarNotFoundException, AvatarAlreadyExistsException {
    final String existingId = "A";
    final Optional<Avatar> currentAvatar = Optional.of(Avatar.builder()
        .id(existingId)
        .userName("Old")
        .build());
    when(repository.findById(existingId)).thenReturn(currentAvatar);
    final String nonExistingUserName = "New";
    when(repository.findByUserName(nonExistingUserName)).thenReturn(null);

    final Avatar updateAvatar = Avatar.builder()
        .id(existingId)
        .userName(nonExistingUserName)
        .imageId("imageIdA")
        .build();
    service.updateAvatar(existingId, updateAvatar);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).findByUserName(nonExistingUserName);
    verify(repository, times(1)).save(updateAvatar);
  }

  @Test
  void updateAvatar_whenNonexistingId_shouldThrowNotFoundException() {
    final String nonExistingId = "A";
    final Optional<Avatar> nonExistingAvatar = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingAvatar);

    final String updateUserName = "New";
    final Avatar updateAvatar = Avatar.builder()
        .id(nonExistingId)
        .userName(updateUserName)
        .imageId("imageIdA")
        .build();
    Exception actual = assertThrows(AvatarNotFoundException.class,
        () -> service.updateAvatar(nonExistingId, updateAvatar));

    String expected = String.format("Avatar with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).findByUserName(updateUserName);
    verify(repository, times(0)).save(updateAvatar);
  }

  @Test
  void updateAvatar_whenExistingUserName_shouldThrowAlreadyExistsException() {
    final String existingId = "A";
    final Optional<Avatar> currentAvatar = Optional.of(
        Avatar.builder().id(existingId).userName("Old").build());
    when(repository.findById(existingId)).thenReturn(currentAvatar);
    final String existingUserName = "New";
    final Avatar existingAvatar = Avatar.builder()
        .id("B")
        .userName(existingUserName)
        .imageId("imageIdB")
        .build();
    when(repository.findByUserName(existingUserName)).thenReturn(existingAvatar);

    final Avatar updateAvatar = Avatar.builder()
        .id(existingId)
        .userName(existingUserName)
        .imageId("imageIdA")
        .build();
    Exception actual = assertThrows(AvatarAlreadyExistsException.class,
        () -> service.updateAvatar(existingId, updateAvatar));

    String expected = String.format("Avatar with userName %s already exists.", existingUserName);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).findByUserName(existingUserName);
    verify(repository, times(0)).save(updateAvatar);
  }

  @Test
  void deleteAvatar_whenExistingId_shouldNotThrowException() throws Exception {
    final String existingId = "A";
    final Optional<Avatar> existingAvatar = Optional
        .of(Avatar.builder().id(existingId).userName("userA").imageId("imageIdA").build());
    when(repository.findById(existingId)).thenReturn(existingAvatar);

    service.deleteAvatar(existingId);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).deleteById(existingId);
  }

  @Test
  void deleteAvatar_whenNonexistingId_shouldThrowNotFoundException() {
    final String nonExistingId = "Z";
    final Optional<Avatar> nonExistingAvatar = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingAvatar);

    Exception actual = assertThrows(AvatarNotFoundException.class, 
        () -> service.deleteAvatar(nonExistingId));

    String expected = String.format("Avatar with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).deleteById(nonExistingId);
  }

}
