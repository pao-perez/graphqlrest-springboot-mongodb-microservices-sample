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
public class ContentController {
  private final ContentService contentService;
  private final ContentMapper contentMapper;

  public ContentController(final ContentService contentService, ContentMapper contentMapper) {
    this.contentService = contentService;
    this.contentMapper = contentMapper;
  }

  @GetMapping()
  public ResponseEntity<ContentsDTO> getAllContents() {
    Collection<ContentDTO> contents =
        contentMapper.contentsToContentsDTO(contentService.getAllContents());
    ContentsDTO contentsDTO = ContentsDTO.builder().data(contents).build();
    return new ResponseEntity<>(contentsDTO, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContentDTO> getContent(final @PathVariable @NotBlank String id)
      throws ContentNotFoundException {
    ContentDTO contentDTO = contentMapper.contentToContentDto(contentService.getContent(id));
    return new ResponseEntity<>(contentDTO, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> createContent(final @RequestBody @Valid ContentDTO contentRequest) {
    Content content = contentMapper.contentDtoToContent(contentRequest);
    String id = contentService.createContent(content);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);

    return new ResponseEntity<>(id, headers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateContent(final @PathVariable @NotBlank String id,
      final @RequestBody @Valid ContentDTO contentRequest)
      throws ContentNotFoundException, ContentMismatchException {
    Content content = contentMapper.contentDtoToContent(contentRequest);
    contentService.updateContent(id, content);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteContent(final @PathVariable @NotBlank String id)
      throws ContentNotFoundException {
    contentService.deleteContent(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
