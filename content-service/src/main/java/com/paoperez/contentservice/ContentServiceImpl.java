package com.paoperez.contentservice;

import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
final class ContentServiceImpl implements ContentService {
    private final ContentRepository repository;
    private final Content.Builder builder;

    ContentServiceImpl(final ContentRepository repository, final Content.Builder builder) {
        this.repository = repository;
        this.builder = builder;
    }

    public Collection<Content> getAllContents() {
        return repository.findAll();
    }

    public Content getContent(final String id) throws ContentNotFoundException {
        return repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
    }

    public Content createContent(final Content content) {
        // TODO : Create builder constructor with existing obj then map
        final Content createContent = builder.avatarId(content.getAvatarId()).categoryId(content.getCategoryId())
                .imageId(content.getImageId()).title(content.getTitle()).body(content.getBody()).rank(content.getRank())
                .withCreated().build();

        return repository.save(createContent);
    }

    public void updateContent(final String id, final Content content) throws ContentNotFoundException {
        // TODO : Create builder constructor with existing obj then map
        repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
        final Content updateContent = builder.id(id).avatarId(content.getAvatarId()).categoryId(content.getCategoryId())
                .imageId(content.getImageId()).title(content.getTitle()).body(content.getBody()).rank(content.getRank())
                .created(content.getCreated()).withUpdated().build();

        repository.save(updateContent);
    }

    public void deleteContent(final String id) throws ContentNotFoundException {
        repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));

        repository.deleteById(id);
    }

}
