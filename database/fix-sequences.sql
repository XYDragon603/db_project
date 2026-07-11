SELECT setval(
    pg_get_serial_sequence('users', 'user_id'),
    COALESCE((SELECT MAX(user_id) FROM users), 1),
    (SELECT COUNT(*) FROM users) > 0
);

SELECT setval(
    pg_get_serial_sequence('roles', 'role_id'),
    COALESCE((SELECT MAX(role_id) FROM roles), 1),
    (SELECT COUNT(*) FROM roles) > 0
);

SELECT setval(
    pg_get_serial_sequence('user_roles', 'user_role_id'),
    COALESCE((SELECT MAX(user_role_id) FROM user_roles), 1),
    (SELECT COUNT(*) FROM user_roles) > 0
);

SELECT setval(
    pg_get_serial_sequence('medications', 'medication_id'),
    COALESCE((SELECT MAX(medication_id) FROM medications), 1),
    (SELECT COUNT(*) FROM medications) > 0
);

SELECT setval(
    pg_get_serial_sequence('prescriptions', 'prescription_id'),
    COALESCE((SELECT MAX(prescription_id) FROM prescriptions), 1),
    (SELECT COUNT(*) FROM prescriptions) > 0
);

SELECT setval(
    pg_get_serial_sequence('medication_schedules', 'schedule_id'),
    COALESCE((SELECT MAX(schedule_id) FROM medication_schedules), 1),
    (SELECT COUNT(*) FROM medication_schedules) > 0
);

SELECT setval(
    pg_get_serial_sequence('dose_logs', 'dose_log_id'),
    COALESCE((SELECT MAX(dose_log_id) FROM dose_logs), 1),
    (SELECT COUNT(*) FROM dose_logs) > 0
);

SELECT setval(
    pg_get_serial_sequence('refill_records', 'refill_id'),
    COALESCE((SELECT MAX(refill_id) FROM refill_records), 1),
    (SELECT COUNT(*) FROM refill_records) > 0
);

SELECT setval(
    pg_get_serial_sequence('caregiver_access', 'access_id'),
    COALESCE((SELECT MAX(access_id) FROM caregiver_access), 1),
    (SELECT COUNT(*) FROM caregiver_access) > 0
);

SELECT setval(
    pg_get_serial_sequence('audit_logs', 'audit_id'),
    COALESCE((SELECT MAX(audit_id) FROM audit_logs), 1),
    (SELECT COUNT(*) FROM audit_logs) > 0
);

SELECT setval(
    pg_get_serial_sequence('medication_catalog', 'catalog_id'),
    COALESCE((SELECT MAX(catalog_id) FROM medication_catalog), 1),
    (SELECT COUNT(*) FROM medication_catalog) > 0
);

SELECT setval(
    pg_get_serial_sequence('medication_brands', 'brand_id'),
    COALESCE((SELECT MAX(brand_id) FROM medication_brands), 1),
    (SELECT COUNT(*) FROM medication_brands) > 0
);
