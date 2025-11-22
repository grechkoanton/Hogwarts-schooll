package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import java.util.Collection;
import java.util.List;

public interface StudentService {

    Student createStudent(Student student);

    Student findStudent(long id);

    Student editStudent(Long id, Student student);

    void deleteStudent(long id);

    Collection<Student> getAllStudents();

    Collection<Student> findByAgeBetween(int minAge, int maxAge);

    Faculty getFacultyByStudentId(Long studentId);

    Integer getTotalNumberOfStudents();

    Double getAverageAge();

    Collection<Student> getLastFiveStudents();

    List<String> getStudentNamesStartingWithA();

    Double getAverageAgeWithStream();
}
