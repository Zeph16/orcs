INSERT INTO programs (name, code) VALUES ('Bachelor of Science', 'BSc');
INSERT INTO programs (name, code) VALUES ('Master of Science', 'MSc');


INSERT INTO departments (name, code, head) VALUES ('Engineering', 'ENG', 'Alice');
INSERT INTO departments (name, code, head) VALUES ('Human Resources', 'HR', 'Bob');
INSERT INTO departments (name, code, head) VALUES ('Finance', 'FIN', 'Charlie');


INSERT INTO department_program (department_id, program_id) VALUES (1, 1); -- Engineering with BSc
INSERT INTO department_program (department_id, program_id) VALUES (1, 2); -- Engineering with MSc
INSERT INTO department_program (department_id, program_id) VALUES (2, 1); -- Human Resources with BSc
INSERT INTO department_program (department_id, program_id) VALUES (3, 2); -- Finance with MSc