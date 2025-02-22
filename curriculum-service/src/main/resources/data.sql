-- Below is a sample seed data
-- DEPs & PROGs
insert into programs (code, name)
values
    ('BSc', 'Undergraduate'),
    ('MSc', 'Graduate');

INSERT INTO departments (name, code, head) VALUES ('Computer Science', 'CS', 'Bob');
INSERT INTO departments (name, code, head) VALUES ('Software Engineering', 'SE', 'Alice');

-- Now include the total_required_credit_hrs
INSERT INTO department_program (department_id, program_id, total_required_credit_hrs) VALUES (1, 1, 120); -- CS with BSc - Example: 120 credit hours
INSERT INTO department_program (department_id, program_id, total_required_credit_hrs) VALUES (1, 2, 60);  -- CS with MSc - Example: 60 credit hours
INSERT INTO department_program (department_id, program_id, total_required_credit_hrs) VALUES (2, 1, 130); -- SE with BSc - Example: 130 credit hours
INSERT INTO department_program (department_id, program_id, total_required_credit_hrs) VALUES (2, 2, 70);  -- SE with MSc - Example: 70 credit hours

-- Insert the courses (updated to include description, course_objectives, and course_content)
INSERT INTO courses (credit_hrs, code, status, title, type, description)
VALUES
    (3, 'CSC101', 'OPEN', 'Introduction to Computer Science', 'MAJOR', 'A foundational course introducing fundamental concepts of computer science.'),
    (5, 'CSC201', 'OPEN', 'Data Structures and Algorithms', 'MAJOR', 'Covers various data structures and algorithms and their applications.'),
    (4, 'CSC202', 'OPEN', 'Object-Oriented Programming', 'MAJOR', 'Introduces the principles and paradigms of object-oriented programming.'),
    (4, 'CSC301', 'OPEN', 'Database Systems', 'MAJOR', 'Covers the design, implementation, and management of database systems.'),
    (4, 'CSC302', 'CLOSED', 'Computer Networks', 'MAJOR', 'Explores the concepts and protocols of computer networks.'),
    (4, 'CSC401', 'OPEN', 'Operating Systems', 'MAJOR', 'Introduces the principles and functionalities of operating systems.'),
    (3, 'CSC402', 'OPEN', 'Artificial Intelligence', 'ELECTIVE', 'Covers the fundamental concepts and techniques of artificial intelligence.'),
    (3, 'CSC403', 'OPEN', 'Software Engineering', 'ELECTIVE', 'Introduces the principles and practices of software engineering.'),
    (3, 'CSC404', 'CLOSED', 'Computer Security', 'ELECTIVE', 'Explores the security threats and vulnerabilities in computer systems.'),
    (3, 'CSC405', 'OPEN', 'Human-Computer Interaction', 'ELECTIVE', 'Focuses on the design and evaluation of user interfaces.');

-- Insert course objectives for CSC101
INSERT INTO course_objectives (course_id, objective)
SELECT courseID, 'Understand basic computer architecture' FROM courses WHERE code = 'CSC101';
INSERT INTO course_objectives (course_id, objective)
SELECT courseID, 'Learn fundamental programming concepts' FROM courses WHERE code = 'CSC101';
INSERT INTO course_objectives (course_id, objective)
SELECT courseID, 'Write simple programs in a high-level language' FROM courses WHERE code = 'CSC101';

-- Insert course content for CSC101
INSERT INTO course_content (course_id, content_item)
SELECT courseID, 'Introduction to computers' FROM courses WHERE code = 'CSC101';
INSERT INTO course_content (course_id, content_item)
SELECT courseID, 'Basic programming concepts' FROM courses WHERE code = 'CSC101';
INSERT INTO course_content (course_id, content_item)
SELECT courseID, 'Control flow statements' FROM courses WHERE code = 'CSC101';

-- Repeat similar INSERT statements for course_objectives and course_content for other courses.
--  (I've omitted them for brevity, but you'll need to add them for each course.)
--  For example, for CSC201:

INSERT INTO course_objectives (course_id, objective)
SELECT courseID, 'Understand different data structures' FROM courses WHERE code = 'CSC201';
INSERT INTO course_objectives (course_id, objective)
SELECT courseID, 'Analyze algorithm efficiency' FROM courses WHERE code = 'CSC201';

INSERT INTO course_content (course_id, content_item)
SELECT courseID, 'Arrays and linked lists' FROM courses WHERE code = 'CSC201';
INSERT INTO course_content (course_id, content_item)
SELECT courseID, 'Searching and sorting algorithms' FROM courses WHERE code = 'CSC201';



-- Insert into course_department_program (You'll need to have data in your department and program tables first)
-- Example (replace with your actual IDs):
-- INSERT INTO course_department_program (course_id, department_id, program_id)
-- SELECT courseID, 1, 1 FROM courses WHERE code = 'CSC101';  -- Assuming department_id 1 and program_id 1 exist

-- Insert into course_prerequisites (You'll need to have data in your courses table first)
-- Example (replace with your actual course IDs):
-- INSERT INTO course_prerequisites (course_id, prerequisite_id)
-- SELECT courseID, (SELECT courseID FROM courses WHERE code = 'CSC101') FROM courses WHERE code = 'CSC201'; -- CSC101 is a prerequisite for CSC201

-- -- Insert the objectives for Software Engineering (CSC403 - id 8)
-- insert into course_course_objectives (course_courseid, course_objectives) values
--                                                          (8, 'have exposure on specialized areas of Software Engineering'),
--                                                          (8, 'have a clear idea on the application of Software Engineering for Development, Education, Manufacturing, etc.'),
--                                                          (8, 'be exposed to office automation and business process modeling'),
--                                                          (8, 'use the Internet and communicate with individuals, companies, and others by sending and receiving messages. Exposure on World Wide Web, Web Design., etc.'),
--                                                          (8, 'be fluent users of graphics, desktop publishing (books, magazines, newspapers and the like) and web browsing'),
--                                                          (8, 'create documents such as personal and commercial letters, memo, project proposals and papers, and so on'),
--                                                          (8, 'work on spread sheets to perform calculations and summarize information');
--
-- -- Insert the content for Software Engineering (CSC403 - id 8)
-- insert into course_course_content (course_courseid, course_content) values
--                                                          (8, 'Introduction'),
--                                                          (8, 'Computer Hardware and Software Evolution, and Computer Architecture'),
--                                                          (8, 'Data Representation and Computer Arithmetic'),
--                                                          (8, 'Programming Languages and the Programming Process'),
--                                                          (8, 'Business Process Engineering'),
--                                                          (8, 'Data Communications and Computer Networks'),
--                                                          (8, 'Internet, Intranet and Extranet'),
--                                                          (8, 'Future Trends in ICT [Reading Assignment]');


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

