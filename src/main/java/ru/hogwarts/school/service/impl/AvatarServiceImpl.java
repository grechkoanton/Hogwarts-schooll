package ru.hogwarts.school.service.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Optional;

@Service
@Transactional
public class AvatarServiceImpl implements AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${avatars.directory}")
    private String avatarsDirectory;

    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student id: {}", studentId);
        logger.debug("Uploading file: {} for student {}", file.getOriginalFilename(), studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with id: {}", studentId);
                    return new RuntimeException("Student not found");
                });

        java.nio.file.Path directory = java.nio.file.Path.of(avatarsDirectory);
        if (!Files.exists(directory)) {
            logger.info("Creating avatars directory: {}", avatarsDirectory);
            Files.createDirectories(directory);
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = studentId + fileExtension;
        java.nio.file.Path filePath = directory.resolve(fileName);
        Files.write(filePath, file.getBytes());

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        Avatar savedAvatar = avatarRepository.save(avatar);
        logger.info("Avatar uploaded successfully for student {} with file path: {}", studentId, filePath);
        return savedAvatar;
    }

    @Override
    public Optional<Avatar> getAvatarByStudentId(Long studentId) {
        logger.info("Was invoked method for get avatar by student id: {}", studentId);
        Optional<Avatar> avatar = avatarRepository.findByStudentId(studentId);

        if (avatar.isEmpty()) {
            logger.warn("Avatar not found for student id: {}", studentId);
        } else {
            logger.debug("Found avatar for student {}: {}", studentId, avatar.get());
        }

        return avatar;
    }

    @Override
    public byte[] getAvatarImageFromDisk(Long studentId) throws IOException {
        logger.info("Was invoked method for get avatar image from disk for student id: {}", studentId);
        Optional<Avatar> avatarOptional = avatarRepository.findByStudentId(studentId);

        if (avatarOptional.isEmpty()) {
            logger.error("Avatar not found for student id: {}", studentId);
            throw new RuntimeException("Avatar not found");
        }

        Avatar avatar = avatarOptional.get();
        java.nio.file.Path filePath = java.nio.file.Path.of(avatar.getFilePath());
        byte[] imageData = Files.readAllBytes(filePath);
        logger.debug("Retrieved avatar image from disk for student {}, file size: {} bytes", studentId, imageData.length);
        return imageData;
    }

    @Override
    public Page<Avatar> getAllAvatars(Integer page, Integer size) {
        logger.info("Was invoked method for get all avatars with pagination - page: {}, size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Avatar> avatars = avatarRepository.findAll(pageRequest);
        logger.debug("Retrieved page {} of avatars, total elements: {}", page, avatars.getTotalElements());
        return avatars;
    }
}
