# MedMinder Database Optimization Notes

## Focus of Optimization

The most important heavy queries in MedMinder are:
- dose history queries
- monthly adherence report queries
- admin audit log queries
- caregiver dashboard access queries

These queries are more expensive because they either scan time ranges, join multiple tables, or filter large event/history tables.

## 1. Dose History Optimization

### Query pattern
The dose history page filters by:
- `user_id`
- date range on `scheduled_datetime`

### Index used
```sql
CREATE INDEX IF NOT EXISTS idx_dose_logs_user_datetime
    ON dose_logs(user_id, scheduled_datetime);
```

### Why it helps
This index supports fast retrieval of one user's history over a chosen date range without scanning the full `dose_logs` table.

## 2. Monthly Adherence Report Optimization

### Query pattern
The report function combines:
- active schedules for a month
- matching medications
- dose log counts grouped by status

### Helpful indexes
```sql
CREATE INDEX IF NOT EXISTS idx_medication_schedules_user
    ON medication_schedules(user_id);

CREATE INDEX IF NOT EXISTS idx_medications_user
    ON medications(user_id);

CREATE INDEX IF NOT EXISTS idx_dose_logs_user_datetime
    ON dose_logs(user_id, scheduled_datetime);
```

### Why they help
- `medication_schedules(user_id)` helps locate the current user's active schedules quickly.
- `medications(user_id)` helps join schedules back to owned medications.
- `dose_logs(user_id, scheduled_datetime)` helps isolate one month of dose events efficiently.

## 3. Audit Log Optimization

### Query pattern
Admin audit log queries often filter by:
- `created_at`
- `user_id`
- `action`

### Indexes used
```sql
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at
    ON audit_logs(created_at);

CREATE INDEX IF NOT EXISTS idx_audit_logs_user_action
    ON audit_logs(user_id, action);
```

### Why they help
- `created_at` supports recent-first and date-range admin review.
- `(user_id, action)` supports targeted audit investigations such as "show all refill updates by one user".

## 4. Caregiver Dashboard Optimization

### Query pattern
Caregiver summary queries filter by:
- `caregiver_id`
- `access_status = 'APPROVED'`

### Index used
```sql
CREATE INDEX IF NOT EXISTS idx_caregiver_access_caregiver_status
    ON caregiver_access(caregiver_id, access_status);
```

### Why it helps
This index quickly narrows the caregiver's visible patient set before joining to user, medication, and dose tables.

## Additional Optimization Notes

- Composite foreign keys in schedules, dose logs, and refill records reduce inconsistent joins.
- Narrow role and ownership filters should be applied before wide aggregations.
- For a student-sized dataset, the current indexes are sufficient.
- If the dataset grows significantly, monthly reporting could later benefit from materialized summaries, but that is not necessary for the first version.
