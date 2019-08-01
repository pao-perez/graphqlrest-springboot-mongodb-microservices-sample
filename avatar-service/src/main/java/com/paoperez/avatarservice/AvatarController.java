package com.paoperez.avatarservice;

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
@RequestMapping("/avatars")
public class AvatarController {
    @Autowired
    private AvatarService avatarService;

    @GetMapping()
    public List<Avatar> getAllAvatars() {
        return avatarService.getAllAvatars();
    }

    @GetMapping("/{id}")
    public Optional<Avatar> getAvatar(@PathVariable String id) {
        return avatarService.getAvatar(id);
    }

    @PostMapping("/create")
    public Avatar createAvatar(@RequestBody Avatar avatar) {
        return avatarService.createAvatar(avatar);
    }

    @PutMapping("/update")
    public Avatar updateAvatar(@RequestBody Avatar avatar) {
        return avatarService.updateAvatar(avatar);
    }

    @DeleteMapping("/{id}")
    public void deleteAvatar(@PathVariable String id) {
        avatarService.deleteAvatar(id);
    }

}
