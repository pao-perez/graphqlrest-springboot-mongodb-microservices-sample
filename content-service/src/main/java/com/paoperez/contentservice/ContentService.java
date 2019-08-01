package com.paoperez.contentservice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService {
    @Autowired
    private ContentRepository contentRepository;

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    public Optional<Content> getContent(String id) {
        return contentRepository.findById(id);
    }

    public Content createContent(Content content) {
        return contentRepository.save(content);
    }

    public Content updateContent(Content content) {
        return contentRepository.save(content);
    }

    public void deleteContent(String id) {
        contentRepository.deleteById(id);
    }

}
