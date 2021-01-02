package com.paoperez.avatarservice;

import java.net.URI;
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
@RequestMapping("/avatars")
class AvatarController {
  private final AvatarService avatarService;

  AvatarController(final AvatarService avatarService) {
    this.avatarService = avatarService;
  }

  @GetMapping()
  ResponseEntity<Avatars> getAllAvatars() {
    Avatars avatars = Avatars.builder().data(avatarService.getAllAvatars()).build();
    return new ResponseEntity<>(avatars, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  ResponseEntity<Avatar> getAvatar(final @PathVariable @NotBlank String id)
      throws AvatarNotFoundException {
    return new ResponseEntity<>(avatarService.getAvatar(id), HttpStatus.OK);
  }

  @PostMapping()
  ResponseEntity<Avatar> createAvatar(final @RequestBody @Valid Avatar avatar)
      throws AvatarAlreadyExistsException {
    Avatar createdAvatar = avatarService.createAvatar(avatar);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdAvatar.getId()).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);

    return new ResponseEntity<>(createdAvatar, headers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  ResponseEntity<Void> updateAvatar(final @PathVariable @NotBlank String id,
      final @RequestBody @Valid Avatar avatar)
      throws AvatarNotFoundException, AvatarAlreadyExistsException {
    avatarService.updateAvatar(id, avatar);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteAvatar(final @PathVariable @NotBlank String id)
      throws AvatarNotFoundException {
    avatarService.deleteAvatar(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
