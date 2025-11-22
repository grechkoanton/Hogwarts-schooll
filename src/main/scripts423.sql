SELECT
    s.student_name AS name,
    s.student_age AS age,
    f.faculty_name AS faculty_name
FROM hogwarts_student s
LEFT JOIN hogwarts_faculty f ON s.faculty_id = f.id;

SELECT
    s.student_name AS name,
    s.student_age AS age
FROM hogwarts_student s
INNER JOIN avatar a ON s.id = a.student_id;