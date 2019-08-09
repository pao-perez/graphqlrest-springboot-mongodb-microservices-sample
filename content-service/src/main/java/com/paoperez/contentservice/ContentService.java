package com.paoperez.contentservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ContentService {
    @Autowired
    private ContentRepository contentRepository;

    List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    Content getContent(String id) {
        return contentRepository.findById(id).orElseThrow(() -> new ContentNotFoundException(id));
    }

    Content createContent(Content content) {
        return contentRepository.save(content);
    }

    Content updateContent(Content content) {
        if (!contentRepository.existsById(content.getId()))
            throw new ContentNotFoundException(content.getId());
        return contentRepository.save(content);
    }

    Boolean deleteContent(String id) {
        if (!contentRepository.existsById(id))
            throw new ContentNotFoundException(id);
        contentRepository.deleteById(id);
        return true;
    }

}
