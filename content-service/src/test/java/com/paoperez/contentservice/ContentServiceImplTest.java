package com.paoperez.contentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
        final Content contentA = Content.builder().avatarId("avatarIdA").categoryId("categoryIdA").imageId("imageIdA")
                .title("Blog A").body("Lorem ipsum dolor").rank(1).id("A").created(LocalDateTime.now().toString())
                .build();
        final Content contentB = Content.builder().avatarId("avatarIdB").categoryId("categoryIdB").imageId("imageIdB")
                .title("Blog B").body("Ut enim ad minim veniam").rank(2).id("B").created(LocalDateTime.now().toString())
                .build();

        final List<Content> expected = ImmutableList.of(contentA, contentB);
        when(repository.findAll()).thenReturn(expected);

        Collection<Content> actual = service.getAllContents();

        assertEquals(expected, actual);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getContent_whenExistingId_shouldReturnContent() {
        final String existingId = "A";
        final Content expected = Content.builder().avatarId("avatarIdA").categoryId("categoryIdA").imageId("imageIdA")
                .title("Blog A").body("Lorem ipsum dolor").rank(1).id(existingId)
                .created(LocalDateTime.now().toString()).build();
        when(repository.findById(existingId)).thenReturn(Optional.of(expected));

        Content actual = service.getContent(existingId);

        assertEquals(expected, actual);
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    void getContent_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "Z";
        final Optional<Content> nonExistingContent = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

        Exception actual = assertThrows(ContentNotFoundException.class, () -> service.getContent(nonExistingId));

        String expected = String.format("Content with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    void createContent_shouldReturnCreatedContent() {
        final String avatarId = "avatarIdA";
        final String categoryId = "categoryIdA";
        final String imageIdA = "imageIdA";
        final String title = "Blog A";
        final String body = "Lorem ipsum dolor";
        final Integer rank = 1;
        final Content newContent = Content.builder().avatarId(avatarId).categoryId(categoryId).imageId(imageIdA)
                .title(title).body(body).rank(rank).build();
        final Content expected = Content.builder().avatarId(avatarId).categoryId(categoryId).imageId(imageIdA)
                .title(title).body(body).rank(rank).created(LocalDateTime.now().toString()).id("A").build();
        when(repository.save(newContent)).thenReturn(expected);

        Content actual = service.createContent(newContent);

        assertEquals(expected, actual);
        verify(repository, times(1)).save(newContent);
    }

    @Test
    void updateContent_whenExistingId_shouldNotThrowException() {
        final String existingId = "A";
        final String avatarId = "avatarIdA";
        final String categoryId = "categoryIdA";
        final String imageIdA = "imageIdA";
        final String title = "Blog A";
        final String body = "Lorem ipsum dolor";
        final Integer rank = 1;
        final String created = LocalDateTime.now().toString();
        final Optional<Content> currentContent = Optional.of(Content.builder().avatarId(avatarId).categoryId(categoryId)
                .imageId(imageIdA).title(title).body(body).rank(rank).id(existingId).created(created).build());
        when(repository.findById(existingId)).thenReturn(currentContent);

        final Content updateContent = Content.builder().avatarId(avatarId).categoryId(categoryId).imageId(imageIdA)
                .title(title).body(body).rank(rank).id(existingId).created(created)
                .updated(LocalDateTime.now().toString()).build();
        service.updateContent(existingId, updateContent);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).save(updateContent);
    }

    @Test
    void updateContent_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "A";
        final Optional<Content> nonExistingContent = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

        final Content updateContent = Content.builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).id(nonExistingId)
                .created(LocalDateTime.now().toString()).build();
        Exception actual = assertThrows(ContentNotFoundException.class,
                () -> service.updateContent(nonExistingId, updateContent));

        String expected = String.format("Content with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).save(updateContent);
    }

    @Test
    void deleteContent_whenExistingId_shouldNotThrowException() {
        final String existingId = "A";
        final Optional<Content> existingContent = Optional.of(Content.builder().avatarId("avatarIdA")
                .categoryId("categoryIdA").imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1)
                .id(existingId).created(LocalDateTime.now().toString()).build());
        when(repository.findById(existingId)).thenReturn(existingContent);

        service.deleteContent(existingId);

        verify(repository, times(1)).findById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteContent_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "Z";
        final Optional<Content> nonExistingContent = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

        Exception actual = assertThrows(ContentNotFoundException.class, () -> service.deleteContent(nonExistingId));

        String expected = String.format("Content with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).deleteById(nonExistingId);
    }

}
