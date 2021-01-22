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
    Avatar avatarA = new Avatar();
    avatarA.setUserName("userA");
    avatarA.setImageId("imageIdA");
    avatarA.setId("A");
    Avatar avatarB = new Avatar();
    avatarB.setUserName("userB");
    avatarB.setImageId("imageIdB");
    avatarB.setId("B");
    List<Avatar> expected = ImmutableList.of(avatarA, avatarB);
    when(repository.findAll()).thenReturn(expected);

    Collection<Avatar> actual = service.getAllAvatars();

    assertEquals(expected, actual);
    verify(repository, times(1)).findAll();
  }

  @Test
  void getAvatar_whenExistingId_shouldReturnAvatar() throws Exception {
    String existingId = "A";
    Avatar expected = new Avatar();
    expected.setUserName("userA");
    expected.setImageId("imageIdA");
    expected.setId("A");
    when(repository.findById(existingId)).thenReturn(Optional.of(expected));

    Avatar actual = service.getAvatar(existingId);

    assertEquals(expected, actual);
    verify(repository, times(1)).findById(existingId);
  }

  @Test
  void getAvatar_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Avatar> nonExistingAvatar = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingAvatar);

    Exception actual =
        assertThrows(AvatarNotFoundException.class, () -> service.getAvatar(nonExistingId));

    String expected = String.format("Avatar with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
  }

  @Test
  void createAvatar_whenNonexistingUserName_shouldReturnCreatedAvatar() throws Exception {
    String nonExistingUserName = "userA";
    when(repository.findByUserName(nonExistingUserName)).thenReturn(null);
    Avatar avatar = new Avatar();
    avatar.setUserName(nonExistingUserName);
    avatar.setImageId("imageIdA");
    Avatar expected = new Avatar();
    expected.setUserName(nonExistingUserName);
    expected.setImageId("imageIdA");
    expected.setId("A");
    when(repository.save(avatar)).thenReturn(expected);

    String actual = service.createAvatar(avatar);

    assertEquals(expected.getId(), actual);
    verify(repository, times(1)).findByUserName(nonExistingUserName);
    verify(repository, times(1)).save(avatar);
  }

  @Test
  void createAvatar_whenExistingUserName_shouldThrowAlreadyExistsException() {
    String existingUserName = "someuser";
    Avatar existingAvatar = new Avatar();
    existingAvatar.setUserName(existingUserName);
    existingAvatar.setImageId("imageIdA");
    existingAvatar.setId("A");
    when(repository.findByUserName(existingUserName)).thenReturn(existingAvatar);
    Avatar avatar = new Avatar();
    avatar.setUserName(existingUserName);
    avatar.setImageId("imageIdB");

    Exception actual =
        assertThrows(AvatarAlreadyExistsException.class, () -> service.createAvatar(avatar));

    String expected = String.format("Avatar with userName %s already exists.", existingUserName);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findByUserName(existingUserName);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateAvatar_whenExistingIdAndNonexistingUserName_shouldNotThrowException()
      throws AvatarNotFoundException, AvatarAlreadyExistsException, AvatarMismatchException {
    String existingId = "A";
    Avatar retrievedAvatar = new Avatar();
    retrievedAvatar.setUserName("Old");
    retrievedAvatar.setImageId("imageIdA");
    retrievedAvatar.setId(existingId);
    when(repository.findById(existingId)).thenReturn(Optional.of(retrievedAvatar));
    String nonExistingUserName = "New";
    when(repository.findByUserName(nonExistingUserName)).thenReturn(null);
    Avatar updateAvatar = new Avatar();
    updateAvatar.setUserName(nonExistingUserName);
    updateAvatar.setImageId("imageIdB");
    updateAvatar.setId(existingId);
    Avatar expected = new Avatar();
    expected.setUserName(nonExistingUserName);
    expected.setImageId("imageIdB");
    expected.setId(existingId);
    when(repository.save(updateAvatar)).thenReturn(expected);

    service.updateAvatar(existingId, updateAvatar);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).findByUserName(nonExistingUserName);
    verify(repository, times(1)).save(updateAvatar);
  }

  @Test
  void updateAvatar_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "A";
    Optional<Avatar> nonExistingAvatar = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingAvatar);
    Avatar updateAvatar = new Avatar();
    updateAvatar.setUserName("userA");
    updateAvatar.setImageId("imageIdA");
    updateAvatar.setId(nonExistingId);

    Exception actual = assertThrows(AvatarNotFoundException.class,
        () -> service.updateAvatar(nonExistingId, updateAvatar));

    String expected = String.format("Avatar with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).findByUserName(null);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateAvatar_whenExistingUserName_shouldThrowAlreadyExistsException() {
    String id = "A";
    Avatar retrievedAvatar = new Avatar();
    retrievedAvatar.setUserName("Old");
    retrievedAvatar.setImageId("imageIdA");
    retrievedAvatar.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(retrievedAvatar));
    String existingUsername = "New";
    Avatar differentAvatar = new Avatar();
    differentAvatar.setUserName(existingUsername);
    differentAvatar.setImageId("imageIdB");
    differentAvatar.setId("differentId");
    when(repository.findByUserName(existingUsername)).thenReturn(differentAvatar);
    Avatar updateAvatar = new Avatar();
    updateAvatar.setUserName(existingUsername);
    updateAvatar.setImageId("imageIdA");
    updateAvatar.setId(id);

    Exception actual = assertThrows(AvatarAlreadyExistsException.class,
        () -> service.updateAvatar(id, updateAvatar));

    String expected = String.format("Avatar with userName %s already exists.", existingUsername);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(1)).findByUserName(existingUsername);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateAvatar_whenMismatchId_shouldThrowMismatchException() {
    String id = "A";
    Avatar retrievedAvatar = new Avatar();
    retrievedAvatar.setUserName("userA");
    retrievedAvatar.setImageId("imageIdA");
    retrievedAvatar.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(retrievedAvatar));
    String differentId = "B";
    Avatar differentAvatar = new Avatar();
    differentAvatar.setUserName("userB");
    differentAvatar.setImageId("imageIdB");
    differentAvatar.setId(differentId);

    Exception actual = assertThrows(AvatarMismatchException.class,
        () -> service.updateAvatar(id, differentAvatar));

    String expected =
        String.format("Avatar with id %s does not match avatar argument %s.", id, differentId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(0)).findByUserName(null);
    verify(repository, times(0)).save(null);
  }

  @Test
  void deleteAvatar_whenExistingId_shouldNotThrowException() throws Exception {
    String existingId = "A";
    Avatar existingAvatar = new Avatar();
    existingAvatar.setUserName("userA");
    existingAvatar.setImageId("imageIdA");
    existingAvatar.setId(existingId);
    when(repository.findById(existingId)).thenReturn(Optional.of(existingAvatar));

    service.deleteAvatar(existingId);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).deleteById(existingId);
  }

  @Test
  void deleteAvatar_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Avatar> nonExistingAvatar = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingAvatar);

    Exception actual =
        assertThrows(AvatarNotFoundException.class, () -> service.deleteAvatar(nonExistingId));

    String expected = String.format("Avatar with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).deleteById(null);
  }
}
