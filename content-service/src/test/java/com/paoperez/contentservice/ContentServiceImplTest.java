package com.paoperez.contentservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.paoperez.contentservice.util.DateTimeFactory;

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

    @MockBean
    private DateTimeFactory dateTimeFactory;

    private Content.Builder builder;

    @BeforeEach
    void init() {
        builder = new Content.Builder(dateTimeFactory);
        service = new ContentServiceImpl(repository, builder);
    }

    @Test
    void getAllContents_shouldReturnContents() {
        final Content contentA = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).id("A")
                .created(LocalDateTime.now().toString()).build();
        final Content contentB = new Content.Builder().avatarId("avatarIdB").categoryId("categoryIdB")
                .imageId("imageIdB").title("Blog B").body("Ut enim ad minim veniam").rank(2).id("B")
                .created(LocalDateTime.now().toString()).build();

        final List<Content> expected = ImmutableList.of(contentA, contentB);
        when(repository.findAll()).thenReturn(expected);

        Collection<Content> actual = service.getAllContents();

        assertEquals(expected, actual);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getContent_whenExistingId_shouldReturnContent() {
        final String existingId = "A";
        final Content expected = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).id(existingId)
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
        final String created = "10/12/2019 08:10AM UTC-800";
        final Content newContent = new Content.Builder().avatarId(avatarId).categoryId(categoryId).imageId(imageIdA)
                .title(title).body(body).rank(rank).build();
        final Content createContent = builder.avatarId(newContent.getAvatarId()).categoryId(newContent.getCategoryId())
                .imageId(newContent.getImageId()).title(newContent.getTitle()).body(newContent.getBody())
                .rank(newContent.getRank()).created(created).build();
        final Content expected = new Content.Builder().avatarId(createContent.getAvatarId())
                .categoryId(createContent.getCategoryId()).imageId(createContent.getImageId())
                .title(createContent.getTitle()).body(createContent.getBody()).rank(createContent.getRank())
                .created(createContent.getCreated()).id("A").build();

        when(dateTimeFactory.dateTime()).thenReturn(created);
        when(repository.save(createContent)).thenReturn(expected);

        Content actual = service.createContent(newContent);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAvatarId(), actual.getAvatarId());
        assertEquals(expected.getImageId(), actual.getImageId());
        assertEquals(expected.getCategoryId(), actual.getCategoryId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getBody(), actual.getBody());
        assertEquals(expected.getRank(), actual.getRank());
        assertEquals(expected.getCreated(), actual.getCreated());
        assertNull(actual.getUpdated());
        assertEquals(expected, actual);
        verify(dateTimeFactory, times(1)).dateTime();
        verify(repository, times(1)).save(createContent);
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
        final String created = "10/15/2019 10:50AM UTC-800";
        final String updated = "10/15/2019 11:30AM UTC-800";
        final Content existingContent = new Content.Builder().avatarId(avatarId).categoryId(categoryId)
                .imageId(imageIdA).title(title).body(body).rank(rank).id(existingId).created(created).build();
        final Optional<Content> currentContent = Optional.of(existingContent);
        final Content updateContent = builder.id(existingId).avatarId(existingContent.getAvatarId())
                .categoryId(existingContent.getCategoryId()).imageId(existingContent.getImageId())
                .title(existingContent.getTitle()).body(existingContent.getBody()).rank(existingContent.getRank())
                .created(existingContent.getCreated()).updated(updated).build();

        when(repository.findById(existingId)).thenReturn(currentContent);
        when(dateTimeFactory.dateTime()).thenReturn(updated);

        service.updateContent(existingId, existingContent);

        verify(repository, times(1)).findById(existingId);
        verify(dateTimeFactory, times(1)).dateTime();
        verify(repository, times(1)).save(updateContent);
    }

    @Test
    void updateContent_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "A";
        final Optional<Content> nonExistingContent = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

        final Content updateContent = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
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
        final Optional<Content> existingContent = Optional.of(new Content.Builder().avatarId("avatarIdA")
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
