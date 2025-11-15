package ru.hogwarts.school.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImpl implements FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyServiceImpl.class);
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty: {}", faculty);
        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty created successfully with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    @Override
    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for find faculty by id: {}", id);
        Optional<Faculty> faculty = facultyRepository.findById(id);

        if (faculty.isEmpty()) {
            logger.warn("Faculty with id {} not found", id);
            logger.error("There is not faculty with id = " + id);
            return null;
        }

        logger.debug("Found faculty: {}", faculty.get());
        return faculty.get();
    }

    @Override
    public Faculty editFaculty(Long id, Faculty faculty) {
        logger.info("Was invoked method for edit faculty with id: {}", id);

        if (!facultyRepository.existsById(id)) {
            logger.warn("Cannot edit faculty - faculty with id {} not found", id);
            return null;
        }

        faculty.setId(id);
        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty with id {} updated successfully", id);
        logger.debug("Updated faculty data: {}", updatedFaculty);
        return updatedFaculty;
    }

    @Override
    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);

        if (!facultyRepository.existsById(id)) {
            logger.warn("Cannot delete faculty - faculty with id {} not found", id);
            return;
        }

        facultyRepository.deleteById(id);
        logger.info("Faculty with id {} deleted successfully", id);
    }

    @Override
    public Collection<Faculty> findByColor(String color) {
        logger.info("Was invoked method for find faculties by color: {}", color);
        Collection<Faculty> faculties = facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    @Override
    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        Collection<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Retrieved {} faculties", faculties.size());
        return faculties;
    }

    @Override
    public Collection<Faculty> findByNameOrColor(String name, String color) {
        logger.info("Was invoked method for find faculties by name or color: name={}, color={}", name, color);
        Collection<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
        logger.debug("Found {} faculties matching name '{}' or color '{}'", faculties.size(), name, color);
        return faculties;
    }

    @Override
    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for get students by faculty id: {}", facultyId);
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);

        if (faculty == null) {
            logger.warn("Faculty with id {} not found when getting students", facultyId);
            return List.of();
        }

        Collection<Student> students = faculty.getStudents();
        logger.debug("Retrieved {} students for faculty {}", students.size(), facultyId);
        return students;
    }
}
