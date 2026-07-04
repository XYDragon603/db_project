package com.medminder.domain.entity;

import com.medminder.domain.enums.DoseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "dose_logs")
public class DoseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dose_log_id")
    private Long doseLogId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MedicationSchedule schedule;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "scheduled_datetime", nullable = false)
    private OffsetDateTime scheduledDatetime;

    @Column(name = "actual_taken_time")
    private OffsetDateTime actualTakenTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoseStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getDoseLogId() { return doseLogId; }
    public void setDoseLogId(Long doseLogId) { this.doseLogId = doseLogId; }
    public MedicationSchedule getSchedule() { return schedule; }
    public void setSchedule(MedicationSchedule schedule) { this.schedule = schedule; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public OffsetDateTime getScheduledDatetime() { return scheduledDatetime; }
    public void setScheduledDatetime(OffsetDateTime scheduledDatetime) { this.scheduledDatetime = scheduledDatetime; }
    public OffsetDateTime getActualTakenTime() { return actualTakenTime; }
    public void setActualTakenTime(OffsetDateTime actualTakenTime) { this.actualTakenTime = actualTakenTime; }
    public DoseStatus getStatus() { return status; }
    public void setStatus(DoseStatus status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
