package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repositories.FacultyRepository;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FacultyControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";
        facultyRepository.deleteAll();
    }

    @Test
    void createFaculty_shouldCreateFacultyAndReturnIt() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("Red");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(faculty.getName());
        assertThat(response.getBody().getColor()).isEqualTo(faculty.getColor());
    }

    @Test
    void getFacultyInfo_shouldReturnFacultyWhenExists() {
        Faculty faculty = new Faculty();
        faculty.setName("Slytherin");
        faculty.setColor("Green");
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, faculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(faculty.getName());
        assertThat(response.getBody().getColor()).isEqualTo(faculty.getColor());
    }

    @Test
    void getFacultyInfo_shouldReturnNotFoundWhenFacultyNotExists() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(baseUrl + "/9999", Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void findFacultiesByNameOrColor_shouldReturnMatchingFaculties() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/filter?nameOrColor=Gryffindor", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getAllFaculties_shouldReturnAllFaculties() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/all", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getStudentsByFacultyId_shouldReturnFacultyStudents() {
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("Blue");
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, faculty, Faculty.class);

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId() + "/students", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editFaculty_shouldUpdateFacultyInfo() {
        Faculty faculty = new Faculty();
        faculty.setName("Original Faculty");
        faculty.setColor("Blue");
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, faculty, Faculty.class);

        Faculty updatedFaculty = new Faculty();
        updatedFaculty.setName("Updated Faculty");
        updatedFaculty.setColor("Red");

        restTemplate.put(baseUrl + "/" + createdFaculty.getId(), updatedFaculty);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(), Faculty.class);
        assertThat(response.getBody().getName()).isEqualTo(updatedFaculty.getName());
        assertThat(response.getBody().getColor()).isEqualTo(updatedFaculty.getColor());
    }

    @Test
    void deleteFaculty_shouldRemoveFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Faculty to Delete");
        faculty.setColor("Yellow");
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, faculty, Faculty.class);

        restTemplate.delete(baseUrl + "/" + createdFaculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(), Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
