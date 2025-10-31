package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    private static final Long FACULTY_ID = 1L;
    private static final String FACULTY_NAME = "Gryffindor";
    private static final String FACULTY_COLOR = "Red";

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        Faculty facultyToCreate = new Faculty(null, FACULTY_NAME, FACULTY_COLOR);
        Faculty createdFaculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(createdFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facultyToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FACULTY_ID))
                .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
    }

    @Test
    void getFacultyInfo_shouldReturnFacultyWhenExists() throws Exception {
        Faculty faculty = new Faculty(FACULTY_ID, FACULTY_NAME, FACULTY_COLOR);
        when(facultyService.findFaculty(FACULTY_ID)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", FACULTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(FACULTY_ID))
                .andExpect(jsonPath("$.name").value(FACULTY_NAME))
                .andExpect(jsonPath("$.color").value(FACULTY_COLOR));
    }

    @Test
    void getFacultyInfo_shouldReturnNotFoundWhenFacultyNotExists() throws Exception {
        when(facultyService.findFaculty(anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findFacultiesByNameOrColor_shouldReturnMatchingFaculties() throws Exception {
        Collection<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Gryffindor", "Red"),
                new Faculty(2L, "Slytherin", "Green"));
        when(facultyService.findByNameOrColor("Gryffindor", "Gryffindor")).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/filter")
                        .param("nameOrColor", "Gryffindor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findFacultiesByNameOrColor_shouldReturnEmptyListWhenNoMatches() throws Exception {
        when(facultyService.findByNameOrColor(anyString(), anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/filter")
                        .param("nameOrColor", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllFaculties_shouldReturnAllFaculties() throws Exception {
        Collection<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Gryffindor", "Red"),
                new Faculty(2L, "Slytherin", "Green"));
        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getStudentsByFacultyId_shouldReturnFacultyStudents() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Harry Potter", 15),
                new Student(2L, "Hermione Granger", 16));
        when(facultyService.getStudentsByFacultyId(FACULTY_ID)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", FACULTY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[1].name").value("Hermione Granger"));
    }

    @Test
    void getStudentsByFacultyId_shouldReturnEmptyListWhenNoStudents() throws Exception {
        when(facultyService.getStudentsByFacultyId(anyLong())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/{id}/students", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void editFaculty_shouldReturnUpdatedFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty(FACULTY_ID, "Updated Name", "Blue");
        when(facultyService.editFaculty(eq(FACULTY_ID), any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/{id}", FACULTY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    void editFaculty_shouldReturnBadRequestWhenFacultyNotExists() throws Exception {
        when(facultyService.editFaculty(eq(999L), any(Faculty.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Faculty())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFaculty_shouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/{id}", FACULTY_ID))
                .andExpect(status().isOk());
    }
}
