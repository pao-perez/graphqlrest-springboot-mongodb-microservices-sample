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
        return repository.save(builder.from(content).withCreated().build());
    }

    public void updateContent(final String id, final Content content) throws ContentNotFoundException {
        repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
        repository.save(builder.from(content).id(id).withUpdated().build());
    }

    public void deleteContent(final String id) throws ContentNotFoundException {
        repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
        repository.deleteById(id);
    }

}
