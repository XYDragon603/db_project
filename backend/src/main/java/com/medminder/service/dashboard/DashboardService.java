package com.medminder.service.dashboard;

import com.medminder.domain.enums.DoseStatus;
import com.medminder.domain.repository.DoseLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import com.medminder.web.dto.AdherenceSummaryResponse;
import com.medminder.web.dto.MedicationTaskResponse;
import com.medminder.web.dto.RefillAlertResponse;
import com.medminder.web.dto.UserDashboardResponse;
import com.medminder.web.DisplayTimeFormatter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final DoseLogRepository doseLogRepository;
    private final MedicationRepository medicationRepository;

    public DashboardService(
        MedicationScheduleRepository medicationScheduleRepository,
        DoseLogRepository doseLogRepository,
        MedicationRepository medicationRepository
    ) {
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.doseLogRepository = doseLogRepository;
        this.medicationRepository = medicationRepository;
    }

    public UserDashboardResponse getDashboard(Long userId) {
        var today = LocalDate.now(ZoneOffset.UTC);
        var startOfDay = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        var endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        var logsToday = doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(
            userId,
            startOfDay,
            endOfDay
        );

        Map<Long, DoseStatus> statusBySchedule = logsToday.stream()
            .collect(Collectors.toMap(log -> log.getSchedule().getScheduleId(), log -> log.getStatus(), (first, second) -> second));

        var schedules = medicationScheduleRepository.findAll().stream()
            .filter(schedule -> schedule.getUser().getUserId().equals(userId))
            .filter(schedule -> schedule.isActive())
            .filter(schedule -> schedule.getStartDate() == null || !schedule.getStartDate().isAfter(today))
            .filter(schedule -> schedule.getEndDate() == null || !schedule.getEndDate().isBefore(today))
            .sorted(Comparator.comparing(schedule -> schedule.getScheduledTime()))
            .toList();

        var tasks = schedules.stream()
            .map(schedule -> new MedicationTaskResponse(
                schedule.getScheduleId(),
                schedule.getMedication().getMedicationId(),
                schedule.getMedication().getMedicineName(),
                schedule.getMedication().getDosage(),
                DisplayTimeFormatter.format(schedule.getScheduledTime()),
                schedule.getDoseAmount().stripTrailingZeros().toPlainString(),
                statusBySchedule.getOrDefault(schedule.getScheduleId(), null) == null
                    ? "PENDING"
                    : statusBySchedule.get(schedule.getScheduleId()).name()
            ))
            .toList();

        var alerts = medicationRepository.findByUserUserIdAndActiveTrueOrderByMedicineNameAsc(userId).stream()
            .filter(medication -> medication.getCurrentQuantity() <= medication.getRefillThreshold())
            .map(medication -> new RefillAlertResponse(
                medication.getMedicationId(),
                medication.getMedicineName(),
                medication.getCurrentQuantity(),
                medication.getRefillThreshold()
            ))
            .toList();

        var monthStart = today.withDayOfMonth(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        var monthEnd = today.with(TemporalAdjusters.lastDayOfMonth()).plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);
        var monthLogs = doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(
            userId,
            monthStart,
            monthEnd
        );
        long taken = monthLogs.stream().filter(log -> log.getStatus() == DoseStatus.TAKEN).count();
        long missed = monthLogs.stream().filter(log -> log.getStatus() == DoseStatus.MISSED).count();
        long skipped = monthLogs.stream().filter(log -> log.getStatus() == DoseStatus.SKIPPED).count();
        long late = monthLogs.stream().filter(log -> log.getStatus() == DoseStatus.LATE).count();
        int totalScheduled = schedules.size() * today.lengthOfMonth();
        double adherenceRate = totalScheduled == 0 ? 0.0 : Math.round((taken * 10000.0) / totalScheduled) / 100.0;

        return new UserDashboardResponse(
            tasks,
            alerts,
            new AdherenceSummaryResponse(totalScheduled, taken, missed, skipped, late, adherenceRate)
        );
    }
}
