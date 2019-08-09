package com.paoperez.contentservice;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
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

@Validated
@RestController
@RequestMapping("/contents")
class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping()
    ResponseEntity<List<Content>> getAllContents() {
        return new ResponseEntity<>(contentService.getAllContents(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<Content> getContent(@PathVariable @NotBlank String id) {
        return new ResponseEntity<>(contentService.getContent(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    ResponseEntity<Content> createContent(@RequestBody @Valid Content content) {
        return new ResponseEntity<>(contentService.createContent(content), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    ResponseEntity<Content> updateContent(@RequestBody @Valid Content content) {
        return new ResponseEntity<>(contentService.updateContent(content), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Boolean> deleteContent(@PathVariable @NotBlank String id) {
        return new ResponseEntity<>(contentService.deleteContent(id), HttpStatus.OK);
    }

}
