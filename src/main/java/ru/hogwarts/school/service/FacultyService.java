package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import java.util.Collection;

public interface FacultyService {

    Faculty createFaculty(Faculty faculty);

    Faculty findFaculty(long id);

    Faculty editFaculty(Long id,Faculty faculty);

    void deleteFaculty(long id);

    Collection<Faculty> findByColor(String color);

    Collection<Faculty> getAllFaculties();

    Collection<Faculty> findByNameOrColor(String name, String color);

    Collection<Student> getStudentsByFacultyId(Long facultyId);

    String getLongestFacultyName();
}
