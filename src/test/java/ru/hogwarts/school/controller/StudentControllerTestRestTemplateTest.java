package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Student;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";
    }

    @Test
    void createStudent_shouldCreateStudentAndReturnIt() {
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(15);

        ResponseEntity<Student> response = restTemplate.postForEntity(baseUrl, student, Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
    }

    @Test
    void getStudentInfo_shouldReturnStudentWhenExists() {
        Student student = new Student();
        student.setName("Hermione Granger");
        student.setAge(16);
        Student createdStudent = restTemplate.postForObject(baseUrl, student, Student.class);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + createdStudent.getId(), Student.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hermione Granger");
    }

    @Test
    void getStudentInfo_shouldReturnNotFoundWhenStudentNotExists() {
        ResponseEntity<Student> response = restTemplate.getForEntity(baseUrl + "/9999", Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findStudentsByAgeBetween_shouldReturnStudentsInAgeRange() {
        Student student1 = new Student();
        student1.setName("Student 15 years");
        student1.setAge(15);
        restTemplate.postForObject(baseUrl, student1, Student.class);

        Student student2 = new Student();
        student2.setName("Student 16 years");
        student2.setAge(16);
        restTemplate.postForObject(baseUrl, student2, Student.class);

        Student student3 = new Student();
        student3.setName("Student 25 years");
        student3.setAge(25);
        restTemplate.postForObject(baseUrl, student3, Student.class);

        ResponseEntity<Collection> response = restTemplate.getForEntity(
                baseUrl + "/age-between?minAge=14&maxAge=18", Collection.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/all", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editStudent_shouldUpdateStudentInfo() {
        Student student = new Student();
        student.setName("Original Name");
        student.setAge(15);
        Student createdStudent = restTemplate.postForObject(baseUrl, student, Student.class);

        Student updatedStudent = new Student();
        updatedStudent.setName("Updated Name");
        updatedStudent.setAge(16);

        restTemplate.put(baseUrl + "/" + createdStudent.getId(), updatedStudent);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + createdStudent.getId(), Student.class);
        assertThat(response.getBody().getName()).isEqualTo("Updated Name");
    }

    @Test
    void deleteStudent_shouldRemoveStudent() {
        Student student = new Student();
        student.setName("Student to Delete");
        student.setAge(15);
        Student createdStudent = restTemplate.postForObject(baseUrl, student, Student.class);

        restTemplate.delete(baseUrl + "/" + createdStudent.getId());

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + createdStudent.getId(), Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
