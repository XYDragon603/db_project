package com.medminder.service.report;

import com.medminder.domain.entity.MedicationSchedule;
import com.medminder.domain.entity.DoseLog;
import com.medminder.domain.enums.DoseStatus;
import com.medminder.domain.repository.DoseLogRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import com.medminder.web.dto.AdherenceSummaryResponse;
import com.medminder.web.dto.MedicationAdherenceSummaryResponse;
import com.medminder.web.dto.MonthlyAdherenceReportResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdherenceReportService {

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final DoseLogRepository doseLogRepository;

    public AdherenceReportService(
        MedicationScheduleRepository medicationScheduleRepository,
        DoseLogRepository doseLogRepository
    ) {
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.doseLogRepository = doseLogRepository;
    }

    public MonthlyAdherenceReportResponse getReport(Long userId, YearMonth month) {
        var monthStart = month.atDay(1);
        var monthEnd = month.atEndOfMonth();
        var start = monthStart.atStartOfDay().atOffset(ZoneOffset.UTC);
        var end = monthEnd.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        var schedules = medicationScheduleRepository.findByUserUserIdAndActiveTrue(userId).stream()
            .filter(schedule -> isActiveInMonth(schedule, monthStart, monthEnd))
            .toList();

        var logs = doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(userId, start, end);
        var logsByMedication = logs.stream()
            .collect(Collectors.groupingBy(log -> log.getSchedule().getMedication().getMedicationId()));

        int totalScheduledDoses = schedules.stream()
            .mapToInt(schedule -> countScheduledDays(schedule, monthStart, monthEnd))
            .sum();

        long taken = countStatus(logs, DoseStatus.TAKEN);
        long missed = countStatus(logs, DoseStatus.MISSED);
        long skipped = countStatus(logs, DoseStatus.SKIPPED);
        long late = countStatus(logs, DoseStatus.LATE);

        var medicationSummaries = schedules.stream()
            .collect(Collectors.groupingBy(
                schedule -> schedule.getMedication().getMedicationId(),
                Collectors.mapping(Function.identity(), Collectors.toList())
            ))
            .values().stream()
            .map(group -> {
                var firstSchedule = group.getFirst();
                var medication = firstSchedule.getMedication();
                var medicationLogs = logsByMedication.getOrDefault(medication.getMedicationId(), List.of());
                int scheduledCount = group.stream()
                    .mapToInt(schedule -> countScheduledDays(schedule, monthStart, monthEnd))
                    .sum();
                long medicationTaken = countStatus(medicationLogs, DoseStatus.TAKEN);
                long medicationMissed = countStatus(medicationLogs, DoseStatus.MISSED);
                long medicationSkipped = countStatus(medicationLogs, DoseStatus.SKIPPED);
                long medicationLate = countStatus(medicationLogs, DoseStatus.LATE);

                return new MedicationAdherenceSummaryResponse(
                    medication.getMedicationId(),
                    medication.getMedicineName(),
                    medication.getDosage(),
                    scheduledCount,
                    medicationTaken,
                    medicationMissed,
                    medicationSkipped,
                    medicationLate,
                    calculateAdherenceRate(medicationTaken, scheduledCount)
                );
            })
            .sorted(Comparator.comparing(MedicationAdherenceSummaryResponse::medicineName))
            .toList();

        return new MonthlyAdherenceReportResponse(
            month.toString(),
            new AdherenceSummaryResponse(
                totalScheduledDoses,
                taken,
                missed,
                skipped,
                late,
                calculateAdherenceRate(taken, totalScheduledDoses)
            ),
            medicationSummaries
        );
    }

    private boolean isActiveInMonth(MedicationSchedule schedule, LocalDate monthStart, LocalDate monthEnd) {
        var startDate = schedule.getStartDate();
        var endDate = schedule.getEndDate();
        return (startDate == null || !startDate.isAfter(monthEnd))
            && (endDate == null || !endDate.isBefore(monthStart));
    }

    private int countScheduledDays(MedicationSchedule schedule, LocalDate monthStart, LocalDate monthEnd) {
        var effectiveStart = max(schedule.getStartDate(), monthStart);
        var effectiveEnd = min(schedule.getEndDate(), monthEnd);
        if (effectiveStart.isAfter(effectiveEnd)) {
            return 0;
        }
        return (int) (effectiveEnd.toEpochDay() - effectiveStart.toEpochDay() + 1);
    }

    private LocalDate max(LocalDate value, LocalDate fallback) {
        if (value == null || value.isBefore(fallback)) {
            return fallback;
        }
        return value;
    }

    private LocalDate min(LocalDate value, LocalDate fallback) {
        if (value == null || value.isAfter(fallback)) {
            return fallback;
        }
        return value;
    }

    private long countStatus(List<DoseLog> logs, DoseStatus status) {
        return logs.stream()
            .filter(log -> log.getStatus() == status)
            .count();
    }

    private double calculateAdherenceRate(long takenCount, int totalScheduledDoses) {
        if (totalScheduledDoses == 0) {
            return 0.0;
        }

        return Math.round((takenCount * 10000.0) / totalScheduledDoses) / 100.0;
    }
}
