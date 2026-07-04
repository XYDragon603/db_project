# Sprint 2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the MedMinder database layer for PostgreSQL and Render compatibility without overbuilding the API surface.

**Architecture:** Sprint 2 establishes the canonical SQL schema in `database/`, adds fake seed data, wires the backend to PostgreSQL through environment variables, and introduces the core JPA domain types that later services and controllers will use. The database remains the source of truth, while Spring Boot is prepared for local and Render deployment.

**Tech Stack:** PostgreSQL, Spring Boot, Spring Data JPA, Hibernate, Maven

---

## File Structure

### Create

- `database/schema.sql`
- `database/seed.sql`
- `database/indexes.sql`
- `backend/src/main/java/com/medminder/domain/enums/RoleName.java`
- `backend/src/main/java/com/medminder/domain/enums/DoseStatus.java`
- `backend/src/main/java/com/medminder/domain/enums/CaregiverAccessStatus.java`
- `backend/src/main/java/com/medminder/domain/enums/ScheduleFrequency.java`
- `backend/src/main/java/com/medminder/domain/entity/User.java`
- `backend/src/main/java/com/medminder/domain/entity/Role.java`
- `backend/src/main/java/com/medminder/domain/entity/UserRole.java`
- `backend/src/main/java/com/medminder/domain/entity/Medication.java`
- `backend/src/main/java/com/medminder/domain/entity/Prescription.java`
- `backend/src/main/java/com/medminder/domain/entity/MedicationSchedule.java`
- `backend/src/main/java/com/medminder/domain/entity/DoseLog.java`
- `backend/src/main/java/com/medminder/domain/entity/RefillRecord.java`
- `backend/src/main/java/com/medminder/domain/entity/CaregiverAccess.java`
- `backend/src/main/java/com/medminder/domain/entity/AuditLog.java`
- `backend/src/main/java/com/medminder/domain/repository/UserRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/MedicationRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/MedicationScheduleRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/DoseLogRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/RefillRecordRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/CaregiverAccessRepository.java`
- `backend/src/main/java/com/medminder/domain/repository/AuditLogRepository.java`
- `.env.example`

### Modify

- `backend/pom.xml`
- `backend/src/main/resources/application.yml`
- `README.md`

## Task 1: Add PostgreSQL and JPA backend support

- [ ] Add Spring Data JPA, PostgreSQL driver, and validation dependency entries to `backend/pom.xml`
- [ ] Update `application.yml` for local and Render environment variable based datasource configuration
- [ ] Keep security simple and unchanged for Sprint 2

## Task 2: Add canonical SQL database artifacts

- [ ] Create `database/schema.sql` with PostgreSQL-compatible tables and constraints
- [ ] Create `database/indexes.sql` with query-focused indexes
- [ ] Create `database/seed.sql` with fake seed data for the main tables
- [ ] Keep `PENDING` derived only, not stored in `dose_logs.status`

## Task 3: Add core JPA enums and entities

- [ ] Create enum types for roles, dose status, caregiver status, and schedule frequency
- [ ] Create core entity classes matching the SQL schema
- [ ] Include soft-delete friendly fields such as `is_active` where planned

## Task 4: Add repository interfaces

- [ ] Create repository interfaces for demo-critical domain access
- [ ] Keep them thin and focused on later Sprint 3 service needs

## Task 5: Add practical environment and setup examples

- [ ] Add `.env.example` for Render or local PostgreSQL configuration
- [ ] Keep `README.md` short and practical with setup and run steps only

## Task 6: Verify Sprint 2 output

- [ ] Run backend compile verification
- [ ] Confirm database SQL files exist and are readable
- [ ] Summarize what is ready for Sprint 3 service and API implementation
