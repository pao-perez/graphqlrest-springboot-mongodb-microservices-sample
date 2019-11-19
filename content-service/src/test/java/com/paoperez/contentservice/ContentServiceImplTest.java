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
    void getContent_whenExistingId_shouldReturnContent() throws ContentNotFoundException {
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

        final String expected = String.format("Content with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    void createContent_shouldReturnCreatedContent() {
        final String created = LocalDateTime.now().toString();
        final Content paramContent = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).build();
        final Content createContent = builder.from(paramContent).created(created).build();
        final Content expected = new Content.Builder().from(createContent).id("A").build();
        when(dateTimeFactory.dateTime()).thenReturn(created);
        when(repository.save(createContent)).thenReturn(expected);

        Content actual = service.createContent(paramContent);

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
    void createContent_whenDateTimeFactoryNull_shouldThrowIllegalStateException() {
        dateTimeFactory = null;
        builder = new Content.Builder(dateTimeFactory);
        service = new ContentServiceImpl(repository, builder);
        final Content paramContent = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).build();
        Exception actual = assertThrows(IllegalStateException.class, () -> service.createContent(paramContent));

        final Content createContent = builder.from(paramContent).created(null).build();
        final String expected = "Failed to create DateTime instance, DateTimeFactory in Builder is null.";
        assertEquals(expected, actual.getMessage());
        verify(repository, times(0)).save(createContent);
    }

    @Test
    void updateContent_whenExistingId_shouldNotThrowException() throws ContentNotFoundException {
        final String existingId = "A";
        final Content paramContent = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).id(existingId)
                .created(LocalDateTime.now().toString()).build();
        final Optional<Content> existingContent = Optional.of(paramContent);
        final String updated = LocalDateTime.now().toString();
        when(repository.findById(existingId)).thenReturn(existingContent);
        when(dateTimeFactory.dateTime()).thenReturn(updated);

        service.updateContent(existingId, paramContent);

        final Content expected = builder.from(paramContent).id(existingId).updated(updated).build();
        verify(repository, times(1)).findById(existingId);
        verify(dateTimeFactory, times(1)).dateTime();
        verify(repository, times(1)).save(expected);
    }

    @Test
    void updateContent_whenDateTimeFactoryNull_shouldThrowIllegalStateException() {
        final String existingId = "A";
        final Content paramContent = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).id(existingId)
                .created(LocalDateTime.now().toString()).build();
        final Optional<Content> existingContent = Optional.of(paramContent);
        when(repository.findById(existingId)).thenReturn(existingContent);

        dateTimeFactory = null;
        builder = new Content.Builder(dateTimeFactory);
        service = new ContentServiceImpl(repository, builder);
        Exception actual = assertThrows(IllegalStateException.class,
                () -> service.updateContent(existingId, paramContent));

        final String expected = "Failed to update DateTime instance, DateTimeFactory in Builder is null.";
        final Content updateContent = builder.from(paramContent).id(existingId).updated(null).build();
        assertEquals(expected, actual.getMessage());
        verify(repository, times(0)).save(updateContent);
    }

    @Test
    void updateContent_whenNonexistingId_shouldThrowNotFoundException() {
        final String nonExistingId = "A";
        final Optional<Content> nonExistingContent = Optional.empty();
        when(repository.findById(nonExistingId)).thenReturn(nonExistingContent);

        final Content paramContent = new Content.Builder().avatarId("avatarIdA").categoryId("categoryIdA")
                .imageId("imageIdA").title("Blog A").body("Lorem ipsum dolor").rank(1).id(nonExistingId)
                .created(LocalDateTime.now().toString()).build();
        Exception actual = assertThrows(ContentNotFoundException.class,
                () -> service.updateContent(nonExistingId, paramContent));

        final String expected = String.format("Content with id %s not found.", nonExistingId);
        final Content updateContent = builder.from(paramContent).id(nonExistingId).updated(null).build();
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).save(updateContent);
    }

    @Test
    void deleteContent_whenExistingId_shouldNotThrowException() throws ContentNotFoundException {
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

        final String expected = String.format("Content with id %s not found.", nonExistingId);
        assertEquals(expected, actual.getMessage());
        verify(repository, times(1)).findById(nonExistingId);
        verify(repository, times(0)).deleteById(nonExistingId);
    }

}
