package com.paoperez.contentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ContentServiceImplTest {
  private ContentService service;

  @MockBean
  private ContentRepository repository;

  @BeforeEach
  void init() {
    service = new ContentServiceImpl(repository);
  }

  @Test
  void getAllContents_shouldReturnContents() {
    long created = new Date().toInstant().toEpochMilli();
    Content contentA = new Content();
    contentA.setAvatarId("avatarIdA");
    contentA.setCategoryId("categoryIdA");
    contentA.setImageId("imageIdA");
    contentA.setTitle("Blog A");
    contentA.setBody("Lorem ipsum dolor");
    contentA.setRank(1);
    contentA.setId("A");
    contentA.setCreated(created);
    Content contentB = new Content();
    contentB.setAvatarId("avatarIdB");
    contentB.setCategoryId("categoryIdB");
    contentB.setImageId("imageIdB");
    contentB.setTitle("Blog B");
    contentB.setBody("Ut enim ad mini m veniam");
    contentB.setRank(2);
    contentB.setId("B");
    contentB.setCreated(created);
    List<Content> expected = ImmutableList.of(contentA, contentB);
    when(repository.findAll()).thenReturn(expected);

    Collection<Content> actual = service.getAllContents();

    assertEquals(expected, actual);
    verify(repository, times(1)).findAll();
  }

  @Test
  void getContent_whenExistingId_shouldReturnContent() throws ContentNotFoundException {
    long created = new Date().toInstant().toEpochMilli();
    String existingId = "A";
    Content expected = new Content();
    expected.setAvatarId("avatarIdA");
    expected.setCategoryId("categoryIdA");
    expected.setImageId("imageIdA");
    expected.setTitle("Blog A");
    expected.setBody("Lorem ipsum dolor");
    expected.setRank(1);
    expected.setId(existingId);
    expected.setCreated(created);
    when(repository.findById(existingId)).thenReturn(Optional.of(expected));

    Content actual = service.getContent(existingId);

    assertEquals(expected, actual);
    verify(repository, times(1)).findById(existingId);
  }

  @Test
  void getContent_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Content> nonExistingContent = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

    Exception actual =
        assertThrows(ContentNotFoundException.class, () -> service.getContent(nonExistingId));

    String expected = String.format("Content with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
  }

  @Test
  void createContent_shouldReturnCreatedContent() {
    Content content = new Content();
    content.setAvatarId("avatarIdA");
    content.setCategoryId("categoryIdA");
    content.setImageId("imageIdA");
    content.setTitle("Blog A");
    content.setBody("Lorem ipsum dolor");
    content.setRank(1);
    Content expected = new Content();
    long created = new Date().toInstant().toEpochMilli();
    expected.setAvatarId("avatarIdA");
    expected.setCategoryId("categoryIdA");
    expected.setImageId("imageIdA");
    expected.setTitle("Blog A");
    expected.setBody("Lorem ipsum dolor");
    expected.setRank(1);
    expected.setCreated(created);
    expected.setId("A");
    when(repository.save(content)).thenReturn(expected);

    String actual = service.createContent(content);

    assertEquals(expected.getId(), actual);
    verify(repository, times(1)).save(content);
  }

  @Test
  void updateContent_whenExistingId_shouldNotThrowException()
      throws ContentNotFoundException, ContentMismatchException {
    long created = new Date().toInstant().toEpochMilli();
    String existingId = "A";
    Content retrievedContent = new Content();
    retrievedContent.setAvatarId("avatarIdA");
    retrievedContent.setCategoryId("categoryIdA");
    retrievedContent.setImageId("imageIdA");
    retrievedContent.setTitle("Blog A");
    retrievedContent.setBody("Lorem ipsum dolor");
    retrievedContent.setRank(1);
    retrievedContent.setId(existingId);
    retrievedContent.setCreated(created);
    when(repository.findById(existingId)).thenReturn(Optional.of(retrievedContent));
    Content updateContent = new Content();
    updateContent.setAvatarId("avatarIdB");
    updateContent.setCategoryId("categoryIdB");
    updateContent.setImageId("imageIdB");
    updateContent.setTitle("Blog B");
    updateContent.setBody("Lorem ipsum");
    updateContent.setRank(2);
    updateContent.setId(existingId);
    updateContent.setCreated(created);
    Content expected = new Content();
    long updated = new Date().toInstant().toEpochMilli();
    expected.setAvatarId("avatarIdB");
    expected.setCategoryId("categoryIdB");
    expected.setImageId("imageIdB");
    expected.setTitle("Blog B");
    expected.setBody("Lorem ipsum");
    expected.setRank(2);
    expected.setId(existingId);
    expected.setCreated(created);
    expected.setUpdated(updated);
    when(repository.save(updateContent)).thenReturn(expected);

    service.updateContent(existingId, updateContent);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).save(updateContent);
  }

  @Test
  void updateContent_whenMismatchId_shouldThrowMismatchException()
      throws ContentNotFoundException, ContentMismatchException {
    String id = "A";
    Content retrievedContent = new Content();
    retrievedContent.setAvatarId("avatarIdA");
    retrievedContent.setCategoryId("categoryIdA");
    retrievedContent.setImageId("imageIdA");
    retrievedContent.setTitle("Blog A");
    retrievedContent.setBody("Lorem ipsum dolor");
    retrievedContent.setRank(1);
    retrievedContent.setId(id);
    retrievedContent.setCreated(new Date().toInstant().toEpochMilli());
    when(repository.findById(id)).thenReturn(Optional.of(retrievedContent));
    String differentId = "B";
    Content differentContent = new Content();
    differentContent.setAvatarId("avatarIdB");
    differentContent.setCategoryId("categoryIdB");
    differentContent.setImageId("imageIdB");
    differentContent.setTitle("Blog B");
    differentContent.setBody("Lorem ipsum");
    differentContent.setRank(2);
    differentContent.setId(differentId);
    differentContent.setCreated(new Date().toInstant().toEpochMilli());

    Exception actual = assertThrows(ContentMismatchException.class,
        () -> service.updateContent(id, differentContent));

    String expected =
        String.format("Content with id %s does not match content argument %s.", id, differentId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(id);
    verify(repository, times(0)).save(null);
  }

  @Test
  void updateContent_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "A";
    Optional<Content> nonExistingContent = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);
    long created = new Date().toInstant().toEpochMilli();
    Content updateContent = new Content();
    updateContent.setAvatarId("avatarIdA");
    updateContent.setCategoryId("categoryIdA");
    updateContent.setImageId("imageIdA");
    updateContent.setTitle("Blog A");
    updateContent.setBody("Lorem ipsum dolor");
    updateContent.setRank(1);
    updateContent.setId(nonExistingId);
    updateContent.setCreated(created);

    Exception actual = assertThrows(ContentNotFoundException.class,
        () -> service.updateContent(nonExistingId, updateContent));

    String expected = String.format("Content with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).save(null);
  }

  @Test
  void deleteContent_whenExistingId_shouldNotThrowException() throws ContentNotFoundException {
    String existingId = "A";
    long created = new Date().toInstant().toEpochMilli();
    Content existingContent = new Content();
    existingContent.setAvatarId("avatarIdA");
    existingContent.setCategoryId("categoryIdA");
    existingContent.setImageId("imageIdA");
    existingContent.setTitle("Blog A");
    existingContent.setBody("Lorem ipsum dolor");
    existingContent.setRank(1);
    existingContent.setId(existingId);
    existingContent.setCreated(created);
    when(repository.findById(existingId)).thenReturn(Optional.of(existingContent));

    service.deleteContent(existingId);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).deleteById(existingId);
  }

  @Test
  void deleteContent_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "Z";
    Optional<Content> nonExistingContent = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

    Exception actual =
        assertThrows(ContentNotFoundException.class, () -> service.deleteContent(nonExistingId));

    String expected = String.format("Content with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).deleteById(nonExistingId);
  }
}
