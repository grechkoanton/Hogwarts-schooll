package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Student;
import java.util.Collection;

public interface StudentService {

    Student createStudent(Student student);

    Student findStudent(long id);

    Student editStudent(Long id, Student student);

    void deleteStudent(long id);

    Collection<Student> findByAge(int age);

    Collection<Student> getAllStudents();
}
