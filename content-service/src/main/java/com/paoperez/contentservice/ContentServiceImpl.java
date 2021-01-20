package com.paoperez.contentservice;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
final class ContentServiceImpl implements ContentService {
  private final ContentRepository repository;

  ContentServiceImpl(final ContentRepository repository) {
    this.repository = repository;
  }

  public Collection<Content> getAllContents() {
    return repository.findAll();
  }

  public Content getContent(final String id) throws ContentNotFoundException {
    return repository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
  }

  public String createContent(final Content content) {
    return repository.save(content).getId();
  }

  public void updateContent(final String id, final Content content)
      throws ContentNotFoundException, ContentMismatchException {
    Optional<Content> retrievedContent = repository.findById(id);
    if (retrievedContent.isEmpty()) {
      throw new ContentNotFoundException(id);
    }
    String contentId = content.getId();
    if (!id.equals(contentId)) {
      throw new ContentMismatchException(id, contentId);
    }
    repository.save(content);
  }

  public void deleteContent(final String id) throws ContentNotFoundException {
    if (repository.findById(id).isEmpty()) {
      throw new ContentNotFoundException(id);
    }
    repository.deleteById(id);
  }
}
