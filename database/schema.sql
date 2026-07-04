CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    role_id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    role_id BIGINT NOT NULL REFERENCES roles(role_id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_roles_user_role UNIQUE (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS medications (
    medication_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    medicine_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    form VARCHAR(50) NOT NULL,
    current_quantity INTEGER NOT NULL DEFAULT 0 CHECK (current_quantity >= 0),
    refill_threshold INTEGER NOT NULL DEFAULT 0 CHECK (refill_threshold >= 0),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    start_date DATE,
    end_date DATE,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_medications_id_user UNIQUE (medication_id, user_id),
    CONSTRAINT chk_medications_date_range
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id BIGSERIAL PRIMARY KEY,
    medication_id BIGINT NOT NULL REFERENCES medications(medication_id),
    prescribed_by VARCHAR(150),
    clinic_name VARCHAR(150),
    issue_date DATE,
    instructions TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medication_schedules (
    schedule_id BIGSERIAL PRIMARY KEY,
    medication_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    scheduled_time TIME NOT NULL,
    dose_amount NUMERIC(10,2) NOT NULL CHECK (dose_amount > 0),
    frequency VARCHAR(20) NOT NULL CHECK (frequency IN ('DAILY')),
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedules_medication_owner
        FOREIGN KEY (medication_id, user_id)
        REFERENCES medications(medication_id, user_id),
    CONSTRAINT uq_schedules_id_user UNIQUE (schedule_id, user_id),
    CONSTRAINT chk_schedules_date_range
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS dose_logs (
    dose_log_id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    scheduled_datetime TIMESTAMPTZ NOT NULL,
    actual_taken_time TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL CHECK (status IN ('TAKEN', 'MISSED', 'SKIPPED', 'LATE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dose_logs_schedule_owner
        FOREIGN KEY (schedule_id, user_id)
        REFERENCES medication_schedules(schedule_id, user_id),
    CONSTRAINT uq_dose_logs_schedule_datetime UNIQUE (schedule_id, scheduled_datetime)
);

CREATE TABLE IF NOT EXISTS refill_records (
    refill_id BIGSERIAL PRIMARY KEY,
    medication_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    refill_date DATE NOT NULL DEFAULT CURRENT_DATE,
    quantity_added INTEGER NOT NULL CHECK (quantity_added > 0),
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refill_records_medication_owner
        FOREIGN KEY (medication_id, user_id)
        REFERENCES medications(medication_id, user_id)
);

CREATE TABLE IF NOT EXISTS caregiver_access (
    access_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    caregiver_id BIGINT NOT NULL REFERENCES users(user_id),
    access_status VARCHAR(20) NOT NULL CHECK (access_status IN ('PENDING', 'APPROVED', 'REVOKED')),
    granted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_caregiver_access_pair UNIQUE (user_id, caregiver_id),
    CONSTRAINT chk_caregiver_access_not_self CHECK (user_id <> caregiver_id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    audit_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    action VARCHAR(50) NOT NULL,
    target_table VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    details TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
