ALTER TABLE hogwarts_student
ADD CONSTRAINT age_check CHECK (student_age >= 16);

ALTER TABLE hogwarts_student
ALTER COLUMN student_name SET NOT NULL;

ALTER TABLE hogwarts_student
ADD CONSTRAINT unique_student_name UNIQUE (student_name);

ALTER TABLE hogwarts_faculty
ADD CONSTRAINT unique_name_color UNIQUE (faculty_name, faculty_color);

ALTER TABLE hogwarts_student
ALTER COLUMN student_age SET DEFAULT 20;