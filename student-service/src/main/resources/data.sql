insert into batches (creation_date, credit_cost, expected_grad_date, "section", department_id, id, program_id, code)
values
    ('2023-09-01', 1000.00, '2027-05-01', 'P', 1, 1, 1,'B2023A1'),
    ('2023-09-01', 1500.00, '2026-05-01', 'Q', 1, 2, 2,'M2023A1');

insert into student ( batch_id, address, card_id, email, enrollment_status, name, phone)
values
    ( 1, '123 Main St, City', '123456789', 'student1@example.com', 'ENROLLED', 'John Doe', '123-456-7890'),
    ( 1, '456 Oak Ave, City', '987654321', 'student2@example.com', 'ENROLLED', 'Jane Smith', '987-654-3210'),
    ( 2, '789 Elm St, City', '555123456', 'student3@example.com', 'ENROLLED', 'David Lee', '555-123-4567');