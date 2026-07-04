INSERT INTO roles (role_id, role_name, description) VALUES
    (1, 'USER', 'Standard medication tracking user'),
    (2, 'CAREGIVER', 'Read-only caregiver role'),
    (3, 'ADMIN', 'System management role')
ON CONFLICT (role_name) DO NOTHING;

INSERT INTO users (user_id, full_name, email, password_hash, phone, is_active) VALUES
    (1, 'Emily Johnson', 'emily@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0101', TRUE),
    (2, 'Daniel Lee', 'daniel@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0102', TRUE),
    (3, 'Olivia Carter', 'olivia@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0103', TRUE),
    (4, 'Michael Brown', 'michael@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0104', TRUE),
    (5, 'Sophia Walker', 'sophia@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0105', TRUE),
    (6, 'Alex Johnson', 'alex.caregiver@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0201', TRUE),
    (7, 'Sarah Nguyen', 'sarah.caregiver@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0202', TRUE),
    (8, 'Admin User', 'admin@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0301', TRUE),
    (9, 'Grace Miller', 'grace@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0106', TRUE),
    (10, 'Ethan Davis', 'ethan@example.com', '$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG', '555-0107', TRUE)
ON CONFLICT (email) DO UPDATE SET
    full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    phone = EXCLUDED.phone,
    is_active = EXCLUDED.is_active;

INSERT INTO user_roles (user_role_id, user_id, role_id) VALUES
    (1, 1, 1),
    (2, 2, 1),
    (3, 3, 1),
    (4, 4, 1),
    (5, 5, 1),
    (6, 6, 2),
    (7, 7, 2),
    (8, 8, 3),
    (9, 9, 1),
    (10, 10, 1)
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO medications (
    medication_id, user_id, medicine_name, dosage, form, current_quantity, refill_threshold, is_active, start_date, end_date, notes
) VALUES
    (101, 1, 'Vitamin C', '500mg', 'Tablet', 18, 5, TRUE, '2026-06-01', NULL, 'Morning supplement'),
    (102, 1, 'Metformin', '500mg', 'Tablet', 4, 5, TRUE, '2026-06-10', NULL, 'Take after meals'),
    (103, 1, 'Atorvastatin', '10mg', 'Tablet', 20, 5, TRUE, '2026-06-15', NULL, 'Evening dose'),
    (104, 1, 'Lisinopril', '5mg', 'Tablet', 12, 7, TRUE, '2026-06-15', NULL, 'Monitor blood pressure'),
    (105, 2, 'Omega 3', '1000mg', 'Capsule', 25, 5, TRUE, '2026-06-05', NULL, 'Daily after breakfast'),
    (106, 2, 'Vitamin D', '2000 IU', 'Tablet', 10, 4, TRUE, '2026-06-05', NULL, 'With lunch'),
    (107, 3, 'Ibuprofen', '200mg', 'Tablet', 16, 6, TRUE, '2026-06-20', '2026-07-20', 'As needed in this demo'),
    (108, 4, 'Amlodipine', '5mg', 'Tablet', 22, 5, TRUE, '2026-05-18', NULL, 'Morning dose'),
    (109, 5, 'Magnesium', '250mg', 'Tablet', 30, 8, TRUE, '2026-06-09', NULL, 'Night routine'),
    (110, 9, 'Calcium', '600mg', 'Tablet', 14, 5, TRUE, '2026-06-11', NULL, 'Twice daily in later sprints')
ON CONFLICT DO NOTHING;

INSERT INTO prescriptions (
    prescription_id, medication_id, prescribed_by, clinic_name, issue_date, instructions
) VALUES
    (201, 102, 'Dr. Patel', 'Riverbend Clinic', '2026-06-10', 'Take after meals'),
    (202, 103, 'Dr. Harris', 'Northside Medical', '2026-06-15', 'Take in the evening'),
    (203, 104, 'Dr. Brooks', 'Healthway Center', '2026-06-15', 'Take once daily'),
    (204, 108, 'Dr. Evans', 'Healthway Center', '2026-05-18', 'Take each morning'),
    (205, 107, 'Dr. Reed', 'Urgent Care Plus', '2026-06-20', 'Use only when needed'),
    (206, 106, 'Dr. Patel', 'Riverbend Clinic', '2026-06-05', 'Take with lunch')
ON CONFLICT DO NOTHING;

INSERT INTO medication_schedules (
    schedule_id, medication_id, user_id, scheduled_time, dose_amount, frequency, start_date, end_date, is_active
) VALUES
    (301, 101, 1, '08:00:00', 1.00, 'DAILY', '2026-06-01', NULL, TRUE),
    (302, 102, 1, '13:00:00', 1.00, 'DAILY', '2026-06-10', NULL, TRUE),
    (303, 103, 1, '20:00:00', 1.00, 'DAILY', '2026-06-15', NULL, TRUE),
    (304, 104, 1, '21:00:00', 1.00, 'DAILY', '2026-06-15', NULL, TRUE),
    (305, 105, 2, '08:30:00', 1.00, 'DAILY', '2026-06-05', NULL, TRUE),
    (306, 106, 2, '12:30:00', 1.00, 'DAILY', '2026-06-05', NULL, TRUE),
    (307, 107, 3, '19:00:00', 1.00, 'DAILY', '2026-06-20', '2026-07-20', TRUE),
    (308, 108, 4, '07:30:00', 1.00, 'DAILY', '2026-05-18', NULL, TRUE),
    (309, 109, 5, '22:00:00', 1.00, 'DAILY', '2026-06-09', NULL, TRUE),
    (310, 110, 9, '09:00:00', 1.00, 'DAILY', '2026-06-11', NULL, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO dose_logs (
    dose_log_id, schedule_id, user_id, scheduled_datetime, actual_taken_time, status
) VALUES
    (401, 301, 1, '2026-07-01T08:00:00Z', '2026-07-01T08:02:00Z', 'TAKEN'),
    (402, 302, 1, '2026-07-01T13:00:00Z', NULL, 'MISSED'),
    (403, 303, 1, '2026-07-01T20:00:00Z', '2026-07-01T20:05:00Z', 'TAKEN'),
    (404, 304, 1, '2026-07-01T21:00:00Z', '2026-07-01T21:30:00Z', 'LATE'),
    (405, 301, 1, '2026-07-02T08:00:00Z', '2026-07-02T08:03:00Z', 'TAKEN'),
    (406, 305, 2, '2026-07-01T08:30:00Z', '2026-07-01T08:40:00Z', 'TAKEN'),
    (407, 306, 2, '2026-07-01T12:30:00Z', NULL, 'SKIPPED'),
    (408, 307, 3, '2026-07-01T19:00:00Z', '2026-07-01T19:10:00Z', 'TAKEN'),
    (409, 308, 4, '2026-07-01T07:30:00Z', '2026-07-01T07:28:00Z', 'TAKEN'),
    (410, 309, 5, '2026-07-01T22:00:00Z', NULL, 'MISSED')
ON CONFLICT DO NOTHING;

INSERT INTO refill_records (
    refill_id, medication_id, user_id, refill_date, quantity_added, note
) VALUES
    (501, 101, 1, '2026-06-22', 20, 'Monthly refill'),
    (502, 102, 1, '2026-07-02', 30, 'Low stock refill'),
    (503, 103, 1, '2026-06-29', 15, 'Routine refill'),
    (504, 105, 2, '2026-06-28', 25, 'Supplements restocked'),
    (505, 106, 2, '2026-06-28', 10, 'Small refill'),
    (506, 107, 3, '2026-06-25', 10, 'Demo refill'),
    (507, 108, 4, '2026-06-18', 20, 'Monthly refill'),
    (508, 109, 5, '2026-06-30', 30, 'Night supplement refill'),
    (509, 110, 9, '2026-06-26', 18, 'Calcium refill'),
    (510, 104, 1, '2026-06-27', 12, 'Pressure medication refill')
ON CONFLICT DO NOTHING;

INSERT INTO caregiver_access (
    access_id, user_id, caregiver_id, access_status, granted_at
) VALUES
    (601, 1, 6, 'APPROVED', '2026-06-20T09:00:00Z'),
    (602, 2, 6, 'APPROVED', '2026-06-21T09:00:00Z'),
    (603, 3, 6, 'REVOKED', '2026-06-19T09:00:00Z'),
    (604, 4, 7, 'APPROVED', '2026-06-17T09:00:00Z'),
    (605, 5, 7, 'PENDING', '2026-06-24T09:00:00Z'),
    (606, 9, 6, 'APPROVED', '2026-06-28T09:00:00Z')
ON CONFLICT DO NOTHING;

INSERT INTO audit_logs (
    audit_id, user_id, action, target_table, target_id, details, created_at
) VALUES
    (701, 1, 'ADD_MEDICATION', 'medications', 104, 'Created Lisinopril medication', '2026-06-15T10:00:00Z'),
    (702, 1, 'UPDATE_MEDICATION', 'medications', 102, 'Adjusted refill threshold', '2026-06-18T11:00:00Z'),
    (703, 1, 'LOG_DOSE', 'dose_logs', 401, 'Logged TAKEN dose', '2026-07-01T08:02:00Z'),
    (704, 1, 'LOG_DOSE', 'dose_logs', 402, 'Logged MISSED dose', '2026-07-01T13:05:00Z'),
    (705, 1, 'ADD_REFILL', 'refill_records', 502, 'Added 30 tablets', '2026-07-02T09:10:00Z'),
    (706, 1, 'GRANT_CAREGIVER_ACCESS', 'caregiver_access', 601, 'Approved caregiver Alex Johnson', '2026-06-20T09:00:00Z'),
    (707, 2, 'LOG_DOSE', 'dose_logs', 406, 'Logged TAKEN dose', '2026-07-01T08:40:00Z'),
    (708, 4, 'ADD_MEDICATION', 'medications', 108, 'Created Amlodipine record', '2026-05-18T08:00:00Z'),
    (709, 8, 'UPDATE_ROLE', 'user_roles', 8, 'Confirmed admin assignment', '2026-06-01T12:00:00Z'),
    (710, 1, 'REVOKE_CAREGIVER_ACCESS', 'caregiver_access', 603, 'Revoked caregiver access for old link', '2026-06-30T16:00:00Z')
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('roles', 'role_id'), COALESCE((SELECT MAX(role_id) FROM roles), 1), true);
SELECT setval(pg_get_serial_sequence('users', 'user_id'), COALESCE((SELECT MAX(user_id) FROM users), 1), true);
SELECT setval(pg_get_serial_sequence('user_roles', 'user_role_id'), COALESCE((SELECT MAX(user_role_id) FROM user_roles), 1), true);
SELECT setval(pg_get_serial_sequence('medications', 'medication_id'), COALESCE((SELECT MAX(medication_id) FROM medications), 1), true);
SELECT setval(pg_get_serial_sequence('prescriptions', 'prescription_id'), COALESCE((SELECT MAX(prescription_id) FROM prescriptions), 1), true);
SELECT setval(pg_get_serial_sequence('medication_schedules', 'schedule_id'), COALESCE((SELECT MAX(schedule_id) FROM medication_schedules), 1), true);
SELECT setval(pg_get_serial_sequence('dose_logs', 'dose_log_id'), COALESCE((SELECT MAX(dose_log_id) FROM dose_logs), 1), true);
SELECT setval(pg_get_serial_sequence('refill_records', 'refill_id'), COALESCE((SELECT MAX(refill_id) FROM refill_records), 1), true);
SELECT setval(pg_get_serial_sequence('caregiver_access', 'access_id'), COALESCE((SELECT MAX(access_id) FROM caregiver_access), 1), true);
SELECT setval(pg_get_serial_sequence('audit_logs', 'audit_id'), COALESCE((SELECT MAX(audit_id) FROM audit_logs), 1), true);
