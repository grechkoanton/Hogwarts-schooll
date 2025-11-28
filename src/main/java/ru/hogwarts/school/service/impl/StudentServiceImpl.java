package ru.hogwarts.school.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student createStudent(Student student) {
        log.info("Was invoked method for create student");
        log.debug("Creating student: {}", student);
        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully with id: {}", savedStudent.getId());
        return savedStudent;
    }

    @Override
    public Student findStudent(long id) {
        log.info("Was invoked method for find student by id: {}", id);
        Optional<Student> student = studentRepository.findById(id);

        if (student.isEmpty()) {
            log.warn("Student with id {} not found", id);
            log.error("There is not student with id = " + id);
            return null;
        }

        log.debug("Found student: {}", student.get());
        return student.get();
    }

    @Override
    public Student editStudent(Long id, Student student) {
        log.info("Was invoked method for edit student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            log.warn("Cannot edit student - student with id {} not found", id);
            return null;
        }

        student.setId(id);
        Student updatedStudent = studentRepository.save(student);
        log.info("Student with id {} updated successfully", id);
        log.debug("Updated student data: {}", updatedStudent);
        return updatedStudent;
    }

    @Override
    public void deleteStudent(long id) {
        log.info("Was invoked method for delete student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            log.warn("Cannot delete student - student with id {} not found", id);
            return;
        }

        studentRepository.deleteById(id);
        log.info("Student with id {} deleted successfully", id);
    }

    @Override
    public Collection<Student> getAllStudents() {
        log.info("Was invoked method for get all students");
        Collection<Student> students = studentRepository.findAll();
        log.debug("Retrieved {} students", students.size());
        return students;
    }

    @Override
    public Collection<Student> findByAgeBetween(int minAge, int maxAge) {
        log.info("Was invoked method for find students by age between {} and {}", minAge, maxAge);
        Collection<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        log.debug("Found {} students in age range {}-{}", students.size(), minAge, maxAge);
        return students;
    }

    @Override
    public Faculty getFacultyByStudentId(Long studentId) {
        log.info("Was invoked method for get faculty by student id: {}", studentId);
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            log.warn("Student with id {} not found when getting faculty", studentId);
            return null;
        }

        Faculty faculty = student.getFaculty();
        log.debug("Retrieved faculty {} for student {}", faculty, studentId);
        return faculty;
    }

    @Override
    public Integer getTotalNumberOfStudents() {
        log.info("Was invoked method for get total number of students");
        Integer count = studentRepository.getTotalNumberOfStudents();
        log.debug("Total number of students: {}", count);
        return count;
    }

    @Override
    public Double getAverageAge() {
        log.info("Was invoked method for get average age of students");
        Double averageAge = studentRepository.getAverageAge();
        log.debug("Average age of students: {}", averageAge);
        return averageAge;
    }

    @Override
    public Collection<Student> getLastFiveStudents() {
        log.info("Was invoked method for get last five students");
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("id").descending());
        Collection<Student> students = studentRepository.findAll(pageRequest).getContent();
        log.debug("Retrieved last {} students", students.size());
        return students;
    }

    @Override
    public List<String> getStudentNamesStartingWithA() {
        log.info("Was invoked method for get student names starting with A");
        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && !name.isEmpty())
                .filter(name -> name.toUpperCase().startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
        log.debug("Found {} names starting with A", names.size());
        return names;
    }

    @Override
    public Double getAverageAgeWithStream() {
        log.info("Was invoked method for get average age using stream");
        Double averageAge = studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
        log.debug("Average age calculated via stream: {}", averageAge);
        return averageAge;
    }

    @Override
    public void printStudentsParallel() {
        log.info("Was invoked method for print students in parallel");
        List<Student> students = studentRepository.findAll();

        if (students.size() < 6) {
            log.warn("Not enough students for parallel printing. Required: 6, found: {}", students.size());
            return;
        }

        // комментарии для себя делаю и оставлю, чтоб было понимание
        // Основной поток - имена 1 и 2 студента
        System.out.println("Main Thread: " + students.get(0).getName());
        System.out.println("Main Thread: " + students.get(1).getName());

        // 1-ый параллельный поток - имена 3 и 4 студента
        Thread thread1 = new Thread(() -> {
            System.out.println("Parallel Thread-1: " + students.get(2).getName());
            System.out.println("Parallel Thread-1: " + students.get(3).getName());
        });

        // 2-ой параллельный поток - имена 5 и 6 студента
        Thread thread2 = new Thread(() -> {
            System.out.println("Parallel Thread-2: " + students.get(4).getName());
            System.out.println("Parallel Thread-2: " + students.get(5).getName());
        });

        // запуск потоков
        thread1.start();
        thread2.start();

        // ожидание завершения потоков
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted", e);
        }
    }

    @Override
    public void printStudentsSynchronized() {
        log.info("Was invoked method for print students in synchronized mode");
        List<Student> students = studentRepository.findAll();

        if (students.size() < 6) {
            log.warn("Not enough students for synchronized printing. Required: 6, found: {}", students.size());
            return;
        }

        printStudentNameSynchronized(students.get(0).getName());
        printStudentNameSynchronized(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            printStudentNameSynchronized(students.get(2).getName());
            printStudentNameSynchronized(students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            printStudentNameSynchronized(students.get(4).getName());
            printStudentNameSynchronized(students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted", e);
        }
    }

     // Синхронизированный метод для вывода имен студентов, коммент для себя
    private synchronized void printStudentNameSynchronized(String name) {
        System.out.println(Thread.currentThread().getName() + ": " + name);
    }
}
