package com.medminder.service.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.DoseLog;
import com.medminder.domain.entity.Medication;
import com.medminder.domain.entity.MedicationSchedule;
import com.medminder.domain.entity.User;
import com.medminder.domain.enums.DoseStatus;
import com.medminder.domain.enums.ScheduleFrequency;
import com.medminder.domain.repository.DoseLogRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AdherenceReportServiceTests {

    private MedicationScheduleRepository medicationScheduleRepository;
    private DoseLogRepository doseLogRepository;
    private AdherenceReportService adherenceReportService;

    @BeforeEach
    void setUp() {
        medicationScheduleRepository = Mockito.mock(MedicationScheduleRepository.class);
        doseLogRepository = Mockito.mock(DoseLogRepository.class);
        adherenceReportService = new AdherenceReportService(medicationScheduleRepository, doseLogRepository);
    }

    @Test
    void calculatesMonthlyCountsAndMedicationSummary() {
        var user = buildUser(1L, "Emily Johnson");
        var metformin = buildMedication(101L, user, "Metformin", "500mg");
        var vitaminC = buildMedication(102L, user, "Vitamin C", "500mg");

        var metforminSchedule = buildSchedule(301L, user, metformin, LocalDate.of(2026, 7, 1), null);
        var vitaminCSchedule = buildSchedule(302L, user, vitaminC, LocalDate.of(2026, 7, 10), null);

        var month = YearMonth.of(2026, 7);
        when(medicationScheduleRepository.findByUserUserIdAndActiveTrue(1L)).thenReturn(List.of(metforminSchedule, vitaminCSchedule));
        when(doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(
            Mockito.eq(1L),
            Mockito.any(),
            Mockito.any()
        )).thenReturn(List.of(
            buildDoseLog(metforminSchedule, user, DoseStatus.TAKEN, OffsetDateTime.of(2026, 7, 1, 8, 0, 0, 0, ZoneOffset.UTC)),
            buildDoseLog(metforminSchedule, user, DoseStatus.MISSED, OffsetDateTime.of(2026, 7, 2, 8, 0, 0, 0, ZoneOffset.UTC)),
            buildDoseLog(vitaminCSchedule, user, DoseStatus.SKIPPED, OffsetDateTime.of(2026, 7, 10, 8, 0, 0, 0, ZoneOffset.UTC)),
            buildDoseLog(vitaminCSchedule, user, DoseStatus.LATE, OffsetDateTime.of(2026, 7, 11, 8, 0, 0, 0, ZoneOffset.UTC))
        ));

        var report = adherenceReportService.getReport(1L, month);

        assertEquals("2026-07", report.month());
        assertEquals(53, report.summary().totalScheduledDoses());
        assertEquals(1, report.summary().totalTakenDoses());
        assertEquals(1, report.summary().totalMissedDoses());
        assertEquals(1, report.summary().totalSkippedDoses());
        assertEquals(1, report.summary().totalLateDoses());
        assertEquals(1.89, report.summary().adherenceRate());
        assertEquals(2, report.medications().size());
        assertEquals("Metformin", report.medications().getFirst().medicineName());
        assertEquals(31, report.medications().getFirst().scheduledDoses());
        assertEquals(1, report.medications().getFirst().takenCount());
    }

    @Test
    void returnsZeroAdherenceForMonthWithoutSchedules() {
        var month = YearMonth.of(2026, 8);
        when(medicationScheduleRepository.findByUserUserIdAndActiveTrue(1L)).thenReturn(List.of());
        when(doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(
            Mockito.eq(1L),
            Mockito.any(),
            Mockito.any()
        )).thenReturn(List.of());

        var report = adherenceReportService.getReport(1L, month);

        assertEquals(0, report.summary().totalScheduledDoses());
        assertEquals(0.0, report.summary().adherenceRate());
        assertEquals(0, report.medications().size());
    }

    private static User buildUser(Long userId, String fullName) {
        var user = new User();
        user.setUserId(userId);
        user.setFullName(fullName);
        user.setActive(true);
        return user;
    }

    private static Medication buildMedication(Long medicationId, User user, String medicineName, String dosage) {
        var medication = new Medication();
        medication.setMedicationId(medicationId);
        medication.setUser(user);
        medication.setMedicineName(medicineName);
        medication.setDosage(dosage);
        medication.setActive(true);
        return medication;
    }

    private static MedicationSchedule buildSchedule(
        Long scheduleId,
        User user,
        Medication medication,
        LocalDate startDate,
        LocalDate endDate
    ) {
        var schedule = new MedicationSchedule();
        schedule.setScheduleId(scheduleId);
        schedule.setUser(user);
        schedule.setMedication(medication);
        schedule.setScheduledTime(LocalTime.of(8, 0));
        schedule.setDoseAmount(BigDecimal.ONE);
        schedule.setFrequency(ScheduleFrequency.DAILY);
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setActive(true);
        return schedule;
    }

    private static DoseLog buildDoseLog(
        MedicationSchedule schedule,
        User user,
        DoseStatus status,
        OffsetDateTime scheduledDatetime
    ) {
        var doseLog = new DoseLog();
        doseLog.setSchedule(schedule);
        doseLog.setUser(user);
        doseLog.setStatus(status);
        doseLog.setScheduledDatetime(scheduledDatetime);
        return doseLog;
    }
}
