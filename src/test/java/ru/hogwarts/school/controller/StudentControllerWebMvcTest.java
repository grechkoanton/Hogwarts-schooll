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
import ru.hogwarts.school.service.StudentService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private static final Long STUDENT_ID = 1L;
    private static final String STUDENT_NAME = "Harry Potter";
    private static final int STUDENT_AGE = 15;

    @Test
    void createStudent_shouldReturnCreatedStudent() throws Exception {
        Student studentToCreate = new Student(null, STUDENT_NAME, STUDENT_AGE);
        Student createdStudent = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE);

        when(studentService.createStudent(any(Student.class))).thenReturn(createdStudent);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STUDENT_ID))
                .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                .andExpect(jsonPath("$.age").value(STUDENT_AGE));
    }

    @Test
    void getStudentInfo_shouldReturnStudentWhenExists() throws Exception {
        Student student = new Student(STUDENT_ID, STUDENT_NAME, STUDENT_AGE);
        when(studentService.findStudent(STUDENT_ID)).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(STUDENT_ID))
                .andExpect(jsonPath("$.name").value(STUDENT_NAME))
                .andExpect(jsonPath("$.age").value(STUDENT_AGE));
    }

    @Test
    void getStudentInfo_shouldReturnNotFoundWhenStudentNotExists() throws Exception {
        when(studentService.findStudent(anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findStudentsByAgeBetween_shouldReturnStudentsInRange() throws Exception {
        Collection<Student> students = Arrays.asList(
                new Student(1L, "Student1", 15),
                new Student(2L, "Student2", 16));
        when(studentService.findByAgeBetween(15, 17)).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age-between")
                        .param("minAge", "15")
                        .param("maxAge", "17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].age").value(15))
                .andExpect(jsonPath("$[1].age").value(16));
    }

    @Test
    void findStudentsByAgeBetween_shouldReturnEmptyListWhenNoStudents() throws Exception {
        when(studentService.findByAgeBetween(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/student/age-between")
                        .param("minAge", "100")
                        .param("maxAge", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllStudents_shouldReturnAllStudents() throws Exception {
        Collection<Student> students = Arrays.asList(
                new Student(1L, "Student1", 15),
                new Student(2L, "Student2", 16));
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFacultyByStudentId_shouldReturnFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Gryffindor", "Red");
        when(studentService.getFacultyByStudentId(STUDENT_ID)).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}/faculty", STUDENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void getFacultyByStudentId_shouldReturnNotFoundWhenFacultyNotExists() throws Exception {
        when(studentService.getFacultyByStudentId(anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}/faculty", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void editStudent_shouldReturnUpdatedStudent() throws Exception {
        Student updatedStudent = new Student(STUDENT_ID, "Updated Name", 20);
        when(studentService.editStudent(eq(STUDENT_ID), any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/{id}", STUDENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void editStudent_shouldReturnBadRequestWhenStudentNotExists() throws Exception {
        when(studentService.editStudent(eq(999L), any(Student.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/student/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Student())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteStudent_shouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/{id}", STUDENT_ID))
                .andExpect(status().isOk());
    }
}
