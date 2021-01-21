package com.paoperez.avatarservice;

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
@RequestMapping("/avatars")
public class AvatarController {
  private final AvatarService avatarService;
  private final AvatarMapper avatarMapper;

  public AvatarController(final AvatarService avatarService, AvatarMapper avatarMapper) {
    this.avatarService = avatarService;
    this.avatarMapper = avatarMapper;
  }

  @GetMapping()
  public ResponseEntity<AvatarsDTO> getAllAvatars() {
    Collection<AvatarDTO> avatars = avatarMapper.avatarsToAvatarDTOs(avatarService.getAllAvatars());
    AvatarsDTO avatarsDTO = AvatarsDTO.builder().data(avatars).build();
    return new ResponseEntity<>(avatarsDTO, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AvatarDTO> getAvatar(final @PathVariable @NotBlank String id)
      throws AvatarNotFoundException {
    AvatarDTO avatarDTO = avatarMapper.avatarToAvatarDto(avatarService.getAvatar(id));
    return new ResponseEntity<>(avatarDTO, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> createAvatar(final @RequestBody @Valid AvatarDTO avatarRequest)
      throws AvatarAlreadyExistsException {
    Avatar avatar = avatarMapper.avatarDtoToAvatar(avatarRequest);
    String id = avatarService.createAvatar(avatar);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(location);

    return new ResponseEntity<>(id, headers, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateAvatar(final @PathVariable @NotBlank String id,
      final @RequestBody @Valid AvatarDTO avatarRequest)
      throws AvatarNotFoundException, AvatarAlreadyExistsException, AvatarMismatchException {
    Avatar avatar = avatarMapper.avatarDtoToAvatar(avatarRequest);
    avatarService.updateAvatar(id, avatar);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAvatar(final @PathVariable @NotBlank String id)
      throws AvatarNotFoundException {
    avatarService.deleteAvatar(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
