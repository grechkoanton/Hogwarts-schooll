package ru.hogwarts.school.service;

import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import java.io.IOException;
import java.util.Optional;

public interface AvatarService {

    Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException;

    Optional<Avatar> getAvatarByStudentId(Long studentId);

    byte[] getAvatarImageFromDisk(Long studentId) throws IOException;
}
