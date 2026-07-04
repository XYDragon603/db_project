package com.medminder.service.dose;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.DoseLog;
import com.medminder.domain.enums.DoseStatus;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.DoseLogRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.dto.DoseHistoryResponse;
import com.medminder.web.dto.DoseLogRequest;
import com.medminder.web.dto.DoseLogResponse;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoseLogService {

    private final DoseLogRepository doseLogRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public DoseLogService(
        DoseLogRepository doseLogRepository,
        MedicationScheduleRepository medicationScheduleRepository,
        UserRepository userRepository,
        AuditLogRepository auditLogRepository
    ) {
        this.doseLogRepository = doseLogRepository;
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public DoseLogResponse logDose(Long userId, DoseLogRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        var schedule = medicationScheduleRepository.findById(request.scheduleId())
            .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!schedule.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Schedule does not belong to the user");
        }

        var status = DoseStatus.valueOf(request.status().toUpperCase());
        var log = new DoseLog();
        log.setUser(user);
        log.setSchedule(schedule);
        log.setStatus(status);
        log.setScheduledDatetime(
            request.scheduledDatetime() == null || request.scheduledDatetime().isBlank()
                ? LocalDate.now(ZoneOffset.UTC).atTime(schedule.getScheduledTime()).atOffset(ZoneOffset.UTC)
                : OffsetDateTime.parse(request.scheduledDatetime())
        );
        if (status == DoseStatus.TAKEN || status == DoseStatus.LATE) {
            log.setActualTakenTime(OffsetDateTime.now(ZoneOffset.UTC));
        }
        var saved = doseLogRepository.save(log);

        var medication = schedule.getMedication();
        if (status == DoseStatus.TAKEN && medication.getCurrentQuantity() > 0) {
            medication.setCurrentQuantity(medication.getCurrentQuantity() - 1);
        }

        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("LOG_DOSE");
        auditLog.setTargetTable("dose_logs");
        auditLog.setTargetId(saved.getDoseLogId());
        auditLog.setDetails("Logged " + status.name() + " for schedule " + schedule.getScheduleId());
        auditLogRepository.save(auditLog);

        return new DoseLogResponse(saved.getDoseLogId(), schedule.getScheduleId(), status.name(), medication.getCurrentQuantity());
    }

    public List<DoseHistoryResponse> getHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        var start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        var end = endDate.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        return doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(userId, start, end).stream()
            .map(log -> new DoseHistoryResponse(
                log.getDoseLogId(),
                log.getScheduledDatetime().toString(),
                log.getSchedule().getMedication().getMedicineName(),
                log.getSchedule().getMedication().getDosage(),
                log.getStatus().name(),
                log.getActualTakenTime() == null ? null : log.getActualTakenTime().toString()
            ))
            .toList();
    }
}
