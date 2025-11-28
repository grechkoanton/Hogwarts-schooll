package ru.hogwarts.school.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty createFaculty(Faculty faculty) {
        log.info("Was invoked method for create faculty");
        log.debug("Creating faculty: {}", faculty);
        Faculty savedFaculty = facultyRepository.save(faculty);
        log.info("Faculty created successfully with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    @Override
    public Faculty findFaculty(long id) {
        log.info("Was invoked method for find faculty by id: {}", id);
        Optional<Faculty> faculty = facultyRepository.findById(id);

        if (faculty.isEmpty()) {
            log.warn("Faculty with id {} not found", id);
            log.error("There is not faculty with id = " + id);
            return null;
        }

        log.debug("Found faculty: {}", faculty.get());
        return faculty.get();
    }

    @Override
    public Faculty editFaculty(Long id, Faculty faculty) {
        log.info("Was invoked method for edit faculty with id: {}", id);

        if (!facultyRepository.existsById(id)) {
            log.warn("Cannot edit faculty - faculty with id {} not found", id);
            return null;
        }

        faculty.setId(id);
        Faculty updatedFaculty = facultyRepository.save(faculty);
        log.info("Faculty with id {} updated successfully", id);
        log.debug("Updated faculty data: {}", updatedFaculty);
        return updatedFaculty;
    }

    @Override
    public void deleteFaculty(long id) {
        log.info("Was invoked method for delete faculty with id: {}", id);

        if (!facultyRepository.existsById(id)) {
            log.warn("Cannot delete faculty - faculty with id {} not found", id);
            return;
        }

        facultyRepository.deleteById(id);
        log.info("Faculty with id {} deleted successfully", id);
    }

    @Override
    public Collection<Faculty> findByColor(String color) {
        log.info("Was invoked method for find faculties by color: {}", color);
        Collection<Faculty> faculties = facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
        log.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    @Override
    public Collection<Faculty> getAllFaculties() {
        log.info("Was invoked method for get all faculties");
        Collection<Faculty> faculties = facultyRepository.findAll();
        log.debug("Retrieved {} faculties", faculties.size());
        return faculties;
    }

    @Override
    public Collection<Faculty> findByNameOrColor(String name, String color) {
        log.info("Was invoked method for find faculties by name or color: name={}, color={}", name, color);
        Collection<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
        log.debug("Found {} faculties matching name '{}' or color '{}'", faculties.size(), name, color);
        return faculties;
    }

    @Override
    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        log.info("Was invoked method for get students by faculty id: {}", facultyId);
        Faculty faculty = facultyRepository.findById(facultyId).orElse(null);

        if (faculty == null) {
            log.warn("Faculty with id {} not found when getting students", facultyId);
            return List.of();
        }

        Collection<Student> students = faculty.getStudents();
        log.debug("Retrieved {} students for faculty {}", students.size(), facultyId);
        return students;
    }

    @Override
    public String getLongestFacultyName() {
        log.info("Was invoked method for get longest faculty name");
        String longestName = facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .filter(name -> name != null)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
        log.debug("Longest faculty name: {}", longestName);
        return longestName;
    }
}
