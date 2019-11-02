package com.paoperez.contentservice;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
final class ContentServiceImpl implements ContentService {
    private final ContentRepository contentRepository;

    ContentServiceImpl(final ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public Collection<Content> getAllContents() {
        return contentRepository.findAll();
    }

    public Content getContent(final String id) throws ContentNotFoundException {
        return contentRepository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
    }

    public Content createContent(final Content content) {
        final Content createContent = Content.builder().avatarId(content.getAvatarId())
                .categoryId(content.getCategoryId()).imageId(content.getImageId()).title(content.getTitle())
                .body(content.getBody()).rank(content.getRank()).created(LocalDateTime.now().toString()).build();

        return contentRepository.save(createContent);
    }

    public void updateContent(final String id, final Content content) throws ContentNotFoundException {
        contentRepository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
        final Content updateContent = Content.builder().id(id).avatarId(content.getAvatarId())
                .categoryId(content.getCategoryId()).imageId(content.getImageId()).title(content.getTitle())
                .body(content.getBody()).rank(content.getRank()).created(content.getCreated())
                .updated(LocalDateTime.now().toString()).build();

        contentRepository.save(updateContent);
    }

    public void deleteContent(final String id) throws ContentNotFoundException {
        contentRepository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));

        contentRepository.deleteById(id);
    }

}
