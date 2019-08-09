package com.paoperez.avatarservice;

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
@RequestMapping("/avatars")
class AvatarController {
    @Autowired
    private AvatarService avatarService;

    @GetMapping()
    ResponseEntity<List<Avatar>> getAllAvatars() {
        return new ResponseEntity<>(avatarService.getAllAvatars(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<Avatar> getAvatar(@PathVariable @NotBlank String id) {
        return new ResponseEntity<>(avatarService.getAvatar(id), HttpStatus.OK);
    }

    @PostMapping("/create")
    ResponseEntity<Avatar> createAvatar(@RequestBody @Valid Avatar avatar) {
        return new ResponseEntity<>(avatarService.createAvatar(avatar), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    ResponseEntity<Avatar> updateAvatar(@RequestBody @Valid Avatar avatar) {
        return new ResponseEntity<>(avatarService.updateAvatar(avatar), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Boolean> deleteAvatar(@PathVariable @NotBlank String id) {
        return new ResponseEntity<>(avatarService.deleteAvatar(id), HttpStatus.OK);
    }

}
