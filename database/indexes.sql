CREATE INDEX IF NOT EXISTS idx_dose_logs_user_datetime
    ON dose_logs(user_id, scheduled_datetime);

CREATE INDEX IF NOT EXISTS idx_medication_schedules_user
    ON medication_schedules(user_id);

CREATE INDEX IF NOT EXISTS idx_medications_user
    ON medications(user_id);

CREATE INDEX IF NOT EXISTS idx_caregiver_access_caregiver_status
    ON caregiver_access(caregiver_id, access_status);

CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at
    ON audit_logs(created_at);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_action
    ON audit_logs(user_id, action);
