# MedMinder

MedMinder is a full-stack medication reminder and tracking web application built for educational demonstration as part of a software engineering and database project.

It helps users manage medications, create daily schedules, log doses, monitor refill alerts, review medication history, and view monthly adherence reports. The platform also includes a read-only caregiver view and admin tools for audit monitoring and user account management.

MedMinder is designed for personal medication tracking and student project demonstration only. It is not intended for clinical decision-making or professional medical use.

## Features

### User App
- User registration and login
- Medication dashboard with today’s scheduled doses
- Add, edit, and deactivate medications
- Daily schedule management
- Dose logging with statuses:
  - `TAKEN`
  - `MISSED`
  - `SKIPPED`
  - `LATE`
- Refill alert tracking
- Dose history
- Monthly adherence reports
- Profile and settings update
- Caregiver access management
- Sign out

### Caregiver App
- Read-only caregiver dashboard
- Approved patient access only
- Patient medication overview
- Missed dose and refill visibility

### Admin App
- Admin audit logs
- User management
- Activate and deactivate user accounts
- Basic role management support
- System activity tracking through audit logs

## Tech Stack

### Frontend
- React
- Vite
- TypeScript
- Tailwind CSS
- Responsive healthcare SaaS-style UI
- Reusable component system

### Backend
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate ORM
- REST API architecture
- Role-based access control

### Database
- PostgreSQL
- Normalized relational schema
- Constraints and indexes
- Seed data for demo use

## Architecture

MedMinder follows a standard full-stack layered architecture:

- `frontend/` contains the React client application
- `backend/` contains the Spring Boot server
- `database/` contains schema, seed, index, and helper SQL scripts
- `.github/workflows/` contains CI and security scan workflows

The backend is organized into:
- controllers
- services
- repositories
- entities
- security configuration
- audit logging

The frontend uses:
- shared layouts
- centralized API client
- role-based routing
- reusable UI components for cards, forms, tables, and states

## Core Business Rules

- `PENDING` is a derived display status only and is not stored in `dose_logs`
- `dose_logs.status` only stores:
  - `TAKEN`
  - `MISSED`
  - `SKIPPED`
  - `LATE`
- Soft deactivation is preferred over hard deletion for important records
- Users can only access their own medication data
- Caregivers can only view approved patient records
- Admin users focus on system management, audit review, and account control

## Deployment

MedMinder is deployment-ready with:
- Frontend on Vercel
- Backend on Render
- PostgreSQL on Render

Environment-based configuration is supported for:
- frontend API base URL
- backend datasource settings
- CORS origin
- demo mock fallback control

## Demo Flow

A typical demo flow includes:
1. Register a new user
2. Sign in
3. Add a medication
4. Create a daily schedule
5. Log a dose
6. View refill alerts
7. View dose history
8. Review adherence reports
9. Update profile
10. Sign out

## Demo Accounts

Seeded demo accounts:

- User: `emily@example.com`
- Caregiver: `alex.caregiver@example.com`
- Admin: `admin@example.com`
- Password: `password`

## Project Note

This project was built to align with both:
- a software system construction course requirement
- a database design and implementation course requirement

It emphasizes:
- full-stack integration
- relational database design
- access control
- auditability
- deployment readiness
- clean, responsive UI design