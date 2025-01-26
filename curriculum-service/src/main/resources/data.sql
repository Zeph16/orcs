-- Below is a sample seed data
-- DEPs & PROGs
insert into programs (code, name)
values
    ('BSc', 'Undergraduate'),
    ('MSc', 'Graduate');

INSERT INTO departments (name, code, head) VALUES ('Computer Science', 'CS', 'Bob');
INSERT INTO departments (name, code, head) VALUES ('Software Engineering', 'SE', 'Alice');

INSERT INTO department_program (department_id, program_id) VALUES (1, 1); -- Computer Science with BSc
INSERT INTO department_program (department_id, program_id) VALUES (1, 2); -- Computer Science with MSc
INSERT INTO department_program (department_id, program_id) VALUES (2, 1); -- Software Engineering with BSc
INSERT INTO department_program (department_id, program_id) VALUES (2, 2); -- Software Engineering with MSc

-- COURSES
insert into courses (credit_hrs, code, status, title, type)
values
    (3, 'CSC101', 'OPEN', 'Introduction to Computer Science', 'MAJOR'),
    (5, 'CSC201', 'OPEN', 'Data Structures and Algorithms', 'MAJOR'),
    (4, 'CSC202', 'OPEN', 'Object-Oriented Programming', 'MAJOR'), -- id 3
    (4, 'CSC301', 'OPEN', 'Database Systems', 'MAJOR'),
    (4, 'CSC302', 'CLOSED', 'Computer Networks', 'MAJOR'), -- id 5
    (4, 'CSC401', 'OPEN', 'Operating Systems', 'MAJOR'),
    (3, 'CSC402', 'OPEN', 'Artificial Intelligence', 'ELECTIVE'),
    (3, 'CSC403', 'OPEN', 'Software Engineering', 'ELECTIVE'), -- id 8
    (3, 'CSC404', 'CLOSED', 'Computer Security', 'ELECTIVE'),
    (3, 'CSC405', 'OPEN', 'Human-Computer Interaction', 'ELECTIVE');

insert into course_prerequisites (course_id, prerequisite_id)
values (7, 1),
       (7, 2),
       (7, 4),
       (4, 1),
       (4, 6);

insert into course_department_program (course_id, department_id, program_id)
values
    (1, 1, 1),
    (2, 1, 1),
    (3, 1, 1),
    (4, 1, 1),
    (5, 1, 2),
    (6, 1, 1),
    (7, 1, 1),
    (8, 1, 2),
    (9, 1, 1),
    (10, 1, 1),
    (1, 1, 2),
    (2, 1, 2),
    (3, 1, 2);

-- TERMS
insert into terms (academic_year, add_end_date, add_start_date, drop_end_date, drop_start_date, enrollment_end_date,
                   enrollment_start_date, code, season)
values
    (2025, '2025-05-15', '2025-05-01', '2025-05-22', '2025-05-08', '2025-05-29', '2025-05-15', 'AUT2025', 'AUTUMN'),
    (2025, '2025-06-19', '2025-06-05', '2025-06-26', '2025-06-12', '2025-06-30', '2025-06-19', 'SPR2025', 'SPRING'),
    (2025, '2025-07-17', '2025-07-03', '2025-07-24', '2025-07-10', '2025-07-30', '2025-07-17', 'WIN2025', 'WINTER');

