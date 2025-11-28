-- liquibase formatted sql

-- changeset antongrechko:create-student-name-index
CREATE INDEX idx_student_name ON hogwarts_student (student_name);

-- changeset antongrechko:create-faculty-name-color-index
CREATE INDEX idx_faculty_name_color ON hogwarts_faculty (faculty_name, faculty_color);