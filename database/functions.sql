-- MedMinder database functions and representative screen queries
-- PostgreSQL-focused source material for the Database course.

CREATE OR REPLACE FUNCTION authenticateUser(p_email VARCHAR)
RETURNS TABLE (
    user_id BIGINT,
    full_name VARCHAR,
    email VARCHAR,
    password_hash VARCHAR,
    is_active BOOLEAN,
    role_name VARCHAR
)
LANGUAGE sql
AS $$
    SELECT u.user_id,
           u.full_name,
           u.email,
           u.password_hash,
           u.is_active,
           r.role_name
    FROM users u
    JOIN user_roles ur ON ur.user_id = u.user_id
    JOIN roles r ON r.role_id = ur.role_id
    WHERE lower(u.email) = lower(p_email);
$$;

CREATE OR REPLACE FUNCTION getTodayMedicationSchedule(p_user_id BIGINT)
RETURNS TABLE (
    schedule_id BIGINT,
    medication_id BIGINT,
    medicine_name VARCHAR,
    dosage VARCHAR,
    form VARCHAR,
    scheduled_time TIME,
    dose_amount NUMERIC,
    display_status VARCHAR,
    actual_taken_time TIMESTAMPTZ
)
LANGUAGE sql
AS $$
    SELECT ms.schedule_id,
           m.medication_id,
           m.medicine_name,
           m.dosage,
           m.form,
           ms.scheduled_time,
           ms.dose_amount,
           COALESCE(dl.status, 'PENDING') AS display_status,
           dl.actual_taken_time
    FROM medication_schedules ms
    JOIN medications m
      ON m.medication_id = ms.medication_id
     AND m.user_id = ms.user_id
    LEFT JOIN dose_logs dl
      ON dl.schedule_id = ms.schedule_id
     AND dl.user_id = ms.user_id
     AND dl.scheduled_datetime::date = CURRENT_DATE
    WHERE ms.user_id = p_user_id
      AND ms.is_active = TRUE
      AND m.is_active = TRUE
      AND (ms.start_date IS NULL OR ms.start_date <= CURRENT_DATE)
      AND (ms.end_date IS NULL OR ms.end_date >= CURRENT_DATE)
      AND (m.start_date IS NULL OR m.start_date <= CURRENT_DATE)
      AND (m.end_date IS NULL OR m.end_date >= CURRENT_DATE)
    ORDER BY ms.scheduled_time;
$$;

CREATE OR REPLACE FUNCTION getUserMedications(p_user_id BIGINT)
RETURNS TABLE (
    medication_id BIGINT,
    medicine_name VARCHAR,
    dosage VARCHAR,
    form VARCHAR,
    current_quantity INTEGER,
    refill_threshold INTEGER,
    is_active BOOLEAN,
    start_date DATE,
    end_date DATE,
    notes TEXT
)
LANGUAGE sql
AS $$
    SELECT medication_id,
           medicine_name,
           dosage,
           form,
           current_quantity,
           refill_threshold,
           is_active,
           start_date,
           end_date,
           notes
    FROM medications
    WHERE user_id = p_user_id
    ORDER BY is_active DESC, medicine_name;
$$;

CREATE OR REPLACE FUNCTION getMedicationCatalog(p_country_code CHAR(2))
RETURNS TABLE (
    catalog_id BIGINT,
    generic_name VARCHAR,
    dosage_form VARCHAR,
    strength VARCHAR,
    prescription_required BOOLEAN,
    brand_id BIGINT,
    brand_name VARCHAR,
    manufacturer VARCHAR,
    local_registration_code VARCHAR
)
LANGUAGE sql
AS $$
    SELECT mc.catalog_id,
           mc.generic_name,
           mc.dosage_form,
           mc.strength,
           mc.prescription_required,
           mb.brand_id,
           mb.brand_name,
           mb.manufacturer,
           mb.local_registration_code
    FROM medication_catalog mc
    LEFT JOIN medication_brands mb
      ON mb.catalog_id = mc.catalog_id
     AND mb.is_active = TRUE
    WHERE mc.country_code = upper(p_country_code)
      AND mc.catalog_status = 'ACTIVE'
    ORDER BY mc.generic_name, mb.brand_name;
$$;

-- Representative insert used by Add Medication.
INSERT INTO medications (
    user_id, catalog_id, medicine_name, dosage, form, current_quantity,
    refill_threshold, is_active, start_date, end_date, notes
) VALUES (
    :user_id, :catalog_id, :medicine_name, :dosage, :form, :current_quantity,
    :refill_threshold, TRUE, :start_date, :end_date, :notes
);

CREATE OR REPLACE FUNCTION getMedicationSchedules(p_user_id BIGINT, p_medication_id BIGINT)
RETURNS TABLE (
    schedule_id BIGINT,
    scheduled_time TIME,
    dose_amount NUMERIC,
    frequency VARCHAR,
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN
)
LANGUAGE sql
AS $$
    SELECT schedule_id,
           scheduled_time,
           dose_amount,
           frequency,
           start_date,
           end_date,
           is_active
    FROM medication_schedules
    WHERE user_id = p_user_id
      AND medication_id = p_medication_id
    ORDER BY is_active DESC, scheduled_time;
$$;

CREATE OR REPLACE FUNCTION logDose(
    p_schedule_id BIGINT,
    p_user_id BIGINT,
    p_status VARCHAR,
    p_scheduled_datetime TIMESTAMPTZ,
    p_actual_taken_time TIMESTAMPTZ DEFAULT NULL
)
RETURNS TABLE (
    dose_log_id BIGINT,
    updated_quantity INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_medication_id BIGINT;
    v_current_quantity INTEGER;
    v_dose_amount NUMERIC;
    v_new_dose_log_id BIGINT;
BEGIN
    SELECT ms.medication_id, ms.dose_amount
      INTO v_medication_id, v_dose_amount
    FROM medication_schedules ms
    WHERE ms.schedule_id = p_schedule_id
      AND ms.user_id = p_user_id
      AND ms.is_active = TRUE;

    IF v_medication_id IS NULL THEN
        RAISE EXCEPTION 'Schedule not found or not owned by user';
    END IF;

    INSERT INTO dose_logs (
        schedule_id, user_id, scheduled_datetime, actual_taken_time, status
    ) VALUES (
        p_schedule_id, p_user_id, p_scheduled_datetime, p_actual_taken_time, p_status
    )
    RETURNING dose_log_id INTO v_new_dose_log_id;

    IF p_status = 'TAKEN' THEN
        UPDATE medications
        SET current_quantity = GREATEST(current_quantity - CEIL(v_dose_amount)::INT, 0),
            updated_at = CURRENT_TIMESTAMP
        WHERE medication_id = v_medication_id
          AND user_id = p_user_id
        RETURNING current_quantity INTO v_current_quantity;
    ELSE
        SELECT current_quantity
          INTO v_current_quantity
        FROM medications
        WHERE medication_id = v_medication_id
          AND user_id = p_user_id;
    END IF;

    INSERT INTO audit_logs (user_id, action, target_table, target_id, details)
    VALUES (
        p_user_id,
        'LOG_DOSE',
        'dose_logs',
        v_new_dose_log_id,
        'Status=' || p_status
    );

    RETURN QUERY SELECT v_new_dose_log_id, v_current_quantity;
END;
$$;

CREATE OR REPLACE FUNCTION getDoseHistory(
    p_user_id BIGINT,
    p_start_date DATE,
    p_end_date DATE
)
RETURNS TABLE (
    dose_log_id BIGINT,
    medicine_name VARCHAR,
    scheduled_datetime TIMESTAMPTZ,
    actual_taken_time TIMESTAMPTZ,
    status VARCHAR
)
LANGUAGE sql
AS $$
    SELECT dl.dose_log_id,
           m.medicine_name,
           dl.scheduled_datetime,
           dl.actual_taken_time,
           dl.status
    FROM dose_logs dl
    JOIN medication_schedules ms
      ON ms.schedule_id = dl.schedule_id
     AND ms.user_id = dl.user_id
    JOIN medications m
      ON m.medication_id = ms.medication_id
     AND m.user_id = ms.user_id
    WHERE dl.user_id = p_user_id
      AND dl.scheduled_datetime::date BETWEEN p_start_date AND p_end_date
    ORDER BY dl.scheduled_datetime DESC;
$$;

CREATE OR REPLACE FUNCTION getRefillAlerts(p_user_id BIGINT)
RETURNS TABLE (
    medication_id BIGINT,
    medicine_name VARCHAR,
    current_quantity INTEGER,
    refill_threshold INTEGER,
    shortage INTEGER
)
LANGUAGE sql
AS $$
    SELECT medication_id,
           medicine_name,
           current_quantity,
           refill_threshold,
           refill_threshold - current_quantity AS shortage
    FROM medications
    WHERE user_id = p_user_id
      AND is_active = TRUE
      AND current_quantity <= refill_threshold
    ORDER BY shortage DESC, medicine_name;
$$;

CREATE OR REPLACE FUNCTION addRefillRecord(
    p_user_id BIGINT,
    p_medication_id BIGINT,
    p_quantity_added INTEGER,
    p_note TEXT DEFAULT NULL
)
RETURNS TABLE (
    refill_id BIGINT,
    updated_quantity INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_refill_id BIGINT;
    v_updated_quantity INTEGER;
    v_medication_exists BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM medications
        WHERE medication_id = p_medication_id
          AND user_id = p_user_id
    ) INTO v_medication_exists;

    IF NOT v_medication_exists THEN
        RAISE EXCEPTION 'Medication not found or not owned by user';
    END IF;

    INSERT INTO refill_records (medication_id, user_id, quantity_added, note)
    VALUES (p_medication_id, p_user_id, p_quantity_added, p_note)
    RETURNING refill_id INTO v_refill_id;

    UPDATE medications
    SET current_quantity = current_quantity + p_quantity_added,
        updated_at = CURRENT_TIMESTAMP
    WHERE medication_id = p_medication_id
      AND user_id = p_user_id
    RETURNING current_quantity INTO v_updated_quantity;

    INSERT INTO audit_logs (user_id, action, target_table, target_id, details)
    VALUES (
        p_user_id,
        'ADD_REFILL',
        'refill_records',
        v_refill_id,
        'Quantity added=' || p_quantity_added
    );

    RETURN QUERY SELECT v_refill_id, v_updated_quantity;
END;
$$;

CREATE OR REPLACE FUNCTION getAdherenceSummary(p_user_id BIGINT, p_month DATE)
RETURNS TABLE (
    month_start DATE,
    total_scheduled_doses INTEGER,
    taken_count INTEGER,
    missed_count INTEGER,
    skipped_count INTEGER,
    late_count INTEGER,
    adherence_rate NUMERIC(5,2)
)
LANGUAGE sql
AS $$
    WITH month_window AS (
        SELECT date_trunc('month', p_month)::date AS month_start,
               (date_trunc('month', p_month) + INTERVAL '1 month - 1 day')::date AS month_end
    ),
    schedule_days AS (
        SELECT GREATEST(
                   0,
                   LEAST(COALESCE(ms.end_date, mw.month_end), mw.month_end)
                   - GREATEST(COALESCE(ms.start_date, mw.month_start), mw.month_start)
                   + 1
               )::INT AS overlap_days
        FROM medication_schedules ms
        CROSS JOIN month_window mw
        JOIN medications m
          ON m.medication_id = ms.medication_id
         AND m.user_id = ms.user_id
        WHERE ms.user_id = p_user_id
          AND ms.is_active = TRUE
          AND m.is_active = TRUE
          AND (ms.start_date IS NULL OR ms.start_date <= mw.month_end)
          AND (ms.end_date IS NULL OR ms.end_date >= mw.month_start)
    ),
    total_schedule_count AS (
        SELECT COALESCE(SUM(overlap_days), 0) AS total_scheduled_doses
        FROM schedule_days
    ),
    dose_counts AS (
        SELECT COUNT(*) FILTER (WHERE status = 'TAKEN') AS taken_count,
               COUNT(*) FILTER (WHERE status = 'MISSED') AS missed_count,
               COUNT(*) FILTER (WHERE status = 'SKIPPED') AS skipped_count,
               COUNT(*) FILTER (WHERE status = 'LATE') AS late_count
        FROM dose_logs dl
        CROSS JOIN month_window mw
        WHERE dl.user_id = p_user_id
          AND dl.scheduled_datetime::date BETWEEN mw.month_start AND mw.month_end
    )
    SELECT mw.month_start,
           tsc.total_scheduled_doses,
           COALESCE(dc.taken_count, 0) AS taken_count,
           COALESCE(dc.missed_count, 0) AS missed_count,
           COALESCE(dc.skipped_count, 0) AS skipped_count,
           COALESCE(dc.late_count, 0) AS late_count,
           CASE
               WHEN tsc.total_scheduled_doses = 0 THEN 0
               ELSE ROUND((COALESCE(dc.taken_count, 0)::NUMERIC / tsc.total_scheduled_doses) * 100, 2)
           END AS adherence_rate
    FROM month_window mw
    CROSS JOIN total_schedule_count tsc
    CROSS JOIN dose_counts dc;
$$;

CREATE OR REPLACE FUNCTION getCaregiverPatientOverview(p_caregiver_id BIGINT)
RETURNS TABLE (
    user_id BIGINT,
    patient_name VARCHAR,
    active_medications INTEGER,
    refill_alerts INTEGER,
    taken_today INTEGER,
    missed_today INTEGER
)
LANGUAGE sql
AS $$
    SELECT u.user_id,
           u.full_name AS patient_name,
           COUNT(DISTINCT m.medication_id) FILTER (WHERE m.is_active = TRUE) AS active_medications,
           COUNT(DISTINCT m.medication_id) FILTER (WHERE m.is_active = TRUE AND m.current_quantity <= m.refill_threshold) AS refill_alerts,
           COUNT(dl.dose_log_id) FILTER (WHERE dl.status = 'TAKEN' AND dl.scheduled_datetime::date = CURRENT_DATE) AS taken_today,
           COUNT(dl.dose_log_id) FILTER (WHERE dl.status = 'MISSED' AND dl.scheduled_datetime::date = CURRENT_DATE) AS missed_today
    FROM caregiver_access ca
    JOIN users u ON u.user_id = ca.user_id
    LEFT JOIN medications m ON m.user_id = u.user_id
    LEFT JOIN medication_schedules ms ON ms.user_id = u.user_id AND ms.medication_id = m.medication_id
    LEFT JOIN dose_logs dl ON dl.user_id = u.user_id AND dl.schedule_id = ms.schedule_id
    WHERE ca.caregiver_id = p_caregiver_id
      AND ca.access_status = 'APPROVED'
    GROUP BY u.user_id, u.full_name
    ORDER BY u.full_name;
$$;

CREATE OR REPLACE FUNCTION getAuditLogs(
    p_user_id BIGINT DEFAULT NULL,
    p_action VARCHAR DEFAULT NULL,
    p_start_date DATE DEFAULT NULL,
    p_end_date DATE DEFAULT NULL
)
RETURNS TABLE (
    audit_id BIGINT,
    user_id BIGINT,
    action VARCHAR,
    target_table VARCHAR,
    target_id BIGINT,
    details TEXT,
    created_at TIMESTAMPTZ
)
LANGUAGE sql
AS $$
    SELECT audit_id,
           user_id,
           action,
           target_table,
           target_id,
           details,
           created_at
    FROM audit_logs
    WHERE (p_user_id IS NULL OR audit_logs.user_id = p_user_id)
      AND (p_action IS NULL OR audit_logs.action = p_action)
      AND (p_start_date IS NULL OR audit_logs.created_at::date >= p_start_date)
      AND (p_end_date IS NULL OR audit_logs.created_at::date <= p_end_date)
    ORDER BY created_at DESC;
$$;
