package ru.hogwarts.school.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;
import java.util.*;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student: {}", student);
        Student savedStudent = studentRepository.save(student);
        logger.info("Student created successfully with id: {}", savedStudent.getId());
        return savedStudent;
    }

    @Override
    public Student findStudent(long id) {
        logger.info("Was invoked method for find student by id: {}", id);
        Optional<Student> student = studentRepository.findById(id);

        if (student.isEmpty()) {
            logger.warn("Student with id {} not found", id);
            logger.error("There is not student with id = " + id);
            return null;
        }

        logger.debug("Found student: {}", student.get());
        return student.get();
    }

    @Override
    public Student editStudent(Long id, Student student) {
        logger.info("Was invoked method for edit student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            logger.warn("Cannot edit student - student with id {} not found", id);
            return null;
        }

        student.setId(id);
        Student updatedStudent = studentRepository.save(student);
        logger.info("Student with id {} updated successfully", id);
        logger.debug("Updated student data: {}", updatedStudent);
        return updatedStudent;
    }

    @Override
    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            logger.warn("Cannot delete student - student with id {} not found", id);
            return;
        }

        studentRepository.deleteById(id);
        logger.info("Student with id {} deleted successfully", id);
    }

    @Override
    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        Collection<Student> students = studentRepository.findAll();
        logger.debug("Retrieved {} students", students.size());
        return students;
    }

    @Override
    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for find students by age between {} and {}", minAge, maxAge);
        Collection<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range {}-{}", students.size(), minAge, maxAge);
        return students;
    }

    @Override
    public Faculty getFacultyByStudentId(Long studentId) {
        logger.info("Was invoked method for get faculty by student id: {}", studentId);
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            logger.warn("Student with id {} not found when getting faculty", studentId);
            return null;
        }

        Faculty faculty = student.getFaculty();
        logger.debug("Retrieved faculty {} for student {}", faculty, studentId);
        return faculty;
    }

    @Override
    public Integer getTotalNumberOfStudents() {
        logger.info("Was invoked method for get total number of students");
        Integer count = studentRepository.getTotalNumberOfStudents();
        logger.debug("Total number of students: {}", count);
        return count;
    }

    @Override
    public Double getAverageAge() {
        logger.info("Was invoked method for get average age of students");
        Double averageAge = studentRepository.getAverageAge();
        logger.debug("Average age of students: {}", averageAge);
        return averageAge;
    }

    @Override
    public Collection<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("id").descending());
        Collection<Student> students = studentRepository.findAll(pageRequest).getContent();
        logger.debug("Retrieved last {} students", students.size());
        return students;
    }
}
