package com.paoperez.contentservice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Replace return values with Resource
@RestController
@RequestMapping("/contents")
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping()
    public List<Content> getAllContents() {
        return contentService.getAllContents();
    }

    @GetMapping("/{id}")
    public Optional<Content> getContent(@PathVariable String id) {
        return contentService.getContent(id);
    }

    @PostMapping("/create")
    public Content createContent(@RequestBody Content content) {
        return contentService.createContent(content);
    }

    @PutMapping("/update")
    public Content updateContent(@RequestBody Content content) {
        return contentService.updateContent(content);
    }

    @DeleteMapping("/{id}")
    public void deleteContent(@PathVariable String id) {
        contentService.deleteContent(id);
    }

}
