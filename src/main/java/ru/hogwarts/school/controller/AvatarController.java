package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile file) throws IOException {
        Avatar avatar = avatarService.uploadAvatar(studentId, file);
        return ResponseEntity.ok(avatar);
    }

    @GetMapping("/{studentId}/from-db")
    public ResponseEntity<byte[]> getAvatarFromDb(@PathVariable Long studentId) {
        Optional<Avatar> avatarOptional = avatarService.getAvatarByStudentId(studentId);
        if (avatarOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Avatar avatar = avatarOptional.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);
        return new ResponseEntity<>(avatar.getData(), headers, HttpStatus.OK);
    }

    @GetMapping("/{studentId}/from-disk")
    public ResponseEntity<byte[]> getAvatarFromDisk(@PathVariable Long studentId) throws IOException {
        byte[] image = avatarService.getAvatarImageFromDisk(studentId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
}
