package com.paoperez.contentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
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
    ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
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
    ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
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
  void createContent_shouldReturnsetCreatedContent() {
    Content paramContent = new Content();
    paramContent.setAvatarId("avatarIdA");
    paramContent.setCategoryId("categoryIdA");
    paramContent.setImageId("imageIdA");
    paramContent.setTitle("Blog A");
    paramContent.setBody("Lorem ipsum dolor");
    paramContent.setRank(1);
    Content expected = new Content();
    ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
    expected.setAvatarId("avatarIdA");
    expected.setCategoryId("categoryIdA");
    expected.setImageId("imageIdA");
    expected.setTitle("Blog A");
    expected.setBody("Lorem ipsum dolor");
    expected.setRank(1);
    expected.setCreated(created);
    expected.setId("A");
    when(repository.save(paramContent)).thenReturn(expected);

    String actual = service.createContent(paramContent);
    assertEquals(expected.getId(), actual);

    verify(repository, times(1)).save(paramContent);
  }

  @Test
  void updateContent_whenExistingId_shouldNotThrowException() throws ContentNotFoundException {
    ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
    String existingId = "A";

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

    Content paramContent = new Content();
    paramContent.setAvatarId("avatarIdB");
    paramContent.setCategoryId("categoryIdB");
    paramContent.setImageId("imageIdB");
    paramContent.setTitle("Blog B");
    paramContent.setBody("Lorem ipsum");
    paramContent.setRank(2);
    paramContent.setId(existingId);
    paramContent.setCreated(created);
    Content expected = new Content();
    ZonedDateTime updated = ZonedDateTime.now(ZoneOffset.UTC);
    expected.setAvatarId("avatarIdB");
    expected.setCategoryId("categoryIdB");
    expected.setImageId("imageIdB");
    expected.setTitle("Blog B");
    expected.setBody("Lorem ipsum");
    expected.setRank(2);
    expected.setId(existingId);
    expected.setCreated(created);
    expected.setUpdated(updated);
    when(repository.save(paramContent)).thenReturn(expected);
    service.updateContent(existingId, paramContent);

    verify(repository, times(1)).findById(existingId);
    verify(repository, times(1)).save(expected);
  }

  @Test
  void updateContent_whenNonexistingId_shouldThrowNotFoundException() {
    String nonExistingId = "A";
    Optional<Content> nonExistingContent = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

    ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
    Content paramContent = new Content();
    paramContent.setAvatarId("avatarIdA");
    paramContent.setCategoryId("categoryIdA");
    paramContent.setImageId("imageIdA");
    paramContent.setTitle("Blog A");
    paramContent.setBody("Lorem ipsum dolor");
    paramContent.setRank(1);
    paramContent.setId(nonExistingId);
    paramContent.setCreated(created);
    Exception actual = assertThrows(ContentNotFoundException.class,
        () -> service.updateContent(nonExistingId, paramContent));
    String expected = String.format("Content with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());

    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).save(paramContent);
  }

  @Test
  void deleteContent_whenExistingId_shouldNotThrowException() throws ContentNotFoundException {
    String existingId = "A";

    ZonedDateTime created = ZonedDateTime.now(ZoneOffset.UTC);
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
    final String nonExistingId = "Z";
    final Optional<Content> nonExistingContent = Optional.empty();
    when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

    Exception actual =
        assertThrows(ContentNotFoundException.class, () -> service.deleteContent(nonExistingId));

    final String expected = String.format("Content with id %s not found.", nonExistingId);
    assertEquals(expected, actual.getMessage());
    verify(repository, times(1)).findById(nonExistingId);
    verify(repository, times(0)).deleteById(nonExistingId);
  }
}
