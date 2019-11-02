package com.paoperez.contentservice;

import java.net.URI;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Validated
@RestController
@RequestMapping("/contents")
class ContentController {
    private final ContentService contentService;

    ContentController(final ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping()
    ResponseEntity<Collection<Content>> getAllContents() {
        return new ResponseEntity<>(contentService.getAllContents(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<Content> getContent(final @PathVariable @NotBlank String id) {
        return new ResponseEntity<>(contentService.getContent(id), HttpStatus.OK);
    }

    @PostMapping()
    ResponseEntity<Content> createContent(final @RequestBody @Valid Content content) {
        Content createdContent = contentService.createContent(content);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdContent.getId()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(createdContent, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateContent(final @PathVariable @NotBlank String id,
            final @RequestBody @Valid Content content) {
        contentService.updateContent(id, content);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteContent(final @PathVariable @NotBlank String id) {
        contentService.deleteContent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
