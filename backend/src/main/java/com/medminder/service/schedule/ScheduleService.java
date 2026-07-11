package com.medminder.service.schedule;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.MedicationSchedule;
import com.medminder.domain.enums.ScheduleFrequency;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.DisplayTimeFormatter;
import com.medminder.web.dto.CreateScheduleRequest;
import com.medminder.web.dto.ScheduleResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ScheduleService {

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MedicationRepository medicationRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public ScheduleService(
        MedicationScheduleRepository medicationScheduleRepository,
        MedicationRepository medicationRepository,
        UserRepository userRepository,
        AuditLogRepository auditLogRepository
    ) {
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.medicationRepository = medicationRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<ScheduleResponse> getSchedules(Long userId, Long medicationId) {
        return medicationScheduleRepository.findByUserUserIdAndMedicationMedicationIdAndActiveTrueOrderByScheduledTimeAsc(userId, medicationId).stream()
            .map(schedule -> new ScheduleResponse(
                schedule.getScheduleId(),
                DisplayTimeFormatter.format(schedule.getScheduledTime()),
                schedule.getDoseAmount().stripTrailingZeros().toPlainString(),
                schedule.getFrequency().name(),
                schedule.isActive()
            ))
            .toList();
    }

    @Transactional
    public ScheduleResponse createSchedule(Long userId, CreateScheduleRequest request) {
        return createScheduleRecord(userId, request);
    }

    @Transactional
    public List<ScheduleResponse> createSchedules(Long userId, List<CreateScheduleRequest> requests) {
        return requests.stream()
            .map(request -> createScheduleRecord(userId, request))
            .toList();
    }

    private ScheduleResponse createScheduleRecord(Long userId, CreateScheduleRequest request) {
        var medication = medicationRepository.findById(request.medicationId())
            .orElseThrow(() -> new IllegalArgumentException("Medication not found"));
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!medication.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Medication does not belong to the user");
        }

        var schedule = new MedicationSchedule();
        schedule.setMedication(medication);
        schedule.setUser(user);
        schedule.setScheduledTime(LocalTime.parse(request.scheduledTime()));
        schedule.setDoseAmount(new BigDecimal(request.doseAmount()));
        schedule.setFrequency(ScheduleFrequency.valueOf(request.frequency().toUpperCase()));
        schedule.setStartDate(parseDate(request.startDate()));
        schedule.setEndDate(parseDate(request.endDate()));
        var saved = medicationScheduleRepository.save(schedule);

        return new ScheduleResponse(
            saved.getScheduleId(),
            DisplayTimeFormatter.format(saved.getScheduledTime()),
            saved.getDoseAmount().stripTrailingZeros().toPlainString(),
            saved.getFrequency().name(),
            saved.isActive()
        );
    }

    @Transactional
    public ScheduleResponse deactivateSchedule(Long userId, Long scheduleId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        var schedule = requireOwnedSchedule(userId, scheduleId);

        schedule.setActive(false);
        var saved = medicationScheduleRepository.save(schedule);

        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction("DEACTIVATE_SCHEDULE");
        auditLog.setTargetTable("medication_schedules");
        auditLog.setTargetId(saved.getScheduleId());
        auditLog.setDetails("Deactivated schedule at " + DisplayTimeFormatter.format(saved.getScheduledTime()));
        auditLogRepository.save(auditLog);

        return new ScheduleResponse(
            saved.getScheduleId(),
            DisplayTimeFormatter.format(saved.getScheduledTime()),
            saved.getDoseAmount().stripTrailingZeros().toPlainString(),
            saved.getFrequency().name(),
            saved.isActive()
        );
    }

    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private MedicationSchedule requireOwnedSchedule(Long userId, Long scheduleId) {
        var schedule = medicationScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        if (!schedule.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own schedule");
        }

        return schedule;
    }
}
