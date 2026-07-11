package com.medminder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.Medication;
import com.medminder.domain.entity.MedicationSchedule;
import com.medminder.domain.entity.User;
import com.medminder.domain.enums.ScheduleFrequency;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import com.medminder.domain.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import com.medminder.service.schedule.ScheduleService;
import com.medminder.web.dto.CreateScheduleRequest;
import com.medminder.web.dto.UpdateScheduleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class ScheduleServiceTests {

    private MedicationScheduleRepository medicationScheduleRepository;
    private MedicationRepository medicationRepository;
    private UserRepository userRepository;
    private AuditLogRepository auditLogRepository;
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        medicationScheduleRepository = Mockito.mock(MedicationScheduleRepository.class);
        medicationRepository = Mockito.mock(MedicationRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        scheduleService = new ScheduleService(
            medicationScheduleRepository,
            medicationRepository,
            userRepository,
            auditLogRepository
        );
    }

    @Test
    void deactivatesOwnedScheduleAndWritesAuditLog() {
        var user = buildUser(1L);
        var medication = buildMedication(10L, user);
        var schedule = buildSchedule(101L, user, medication);

        when(medicationScheduleRepository.findById(101L)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> invocation.getArgument(0)).when(medicationScheduleRepository).save(any(MedicationSchedule.class));

        var response = scheduleService.deactivateSchedule(1L, 101L);

        assertFalse(response.active());
        assertFalse(schedule.isActive());

        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals("DEACTIVATE_SCHEDULE", auditCaptor.getValue().getAction());
        assertEquals("medication_schedules", auditCaptor.getValue().getTargetTable());
        assertEquals(101L, auditCaptor.getValue().getTargetId());
    }

    @Test
    void rejectsDeactivatingScheduleOwnedByAnotherUser() {
        var owner = buildUser(2L);
        var medication = buildMedication(10L, owner);
        var schedule = buildSchedule(101L, owner, medication);

        when(medicationScheduleRepository.findById(101L)).thenReturn(Optional.of(schedule));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));

        assertThrows(ResponseStatusException.class, () -> scheduleService.deactivateSchedule(1L, 101L));
    }

    @Test
    void createsMultipleDailySchedulesInOneServiceCall() {
        var user = buildUser(1L);
        var medication = buildMedication(10L, user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(medicationRepository.findById(10L)).thenReturn(Optional.of(medication));
        doAnswer(invocation -> {
            var schedule = invocation.getArgument(0, MedicationSchedule.class);
            schedule.setScheduleId(schedule.getScheduledTime().equals(LocalTime.of(8, 0)) ? 101L : 102L);
            return schedule;
        }).when(medicationScheduleRepository).save(any(MedicationSchedule.class));

        var responses = scheduleService.createSchedules(1L, List.of(
            new CreateScheduleRequest(10L, "08:00", "1", "DAILY", null, null),
            new CreateScheduleRequest(10L, "22:00", "1", "DAILY", null, null)
        ));

        assertEquals(2, responses.size());
        assertEquals("08:00", responses.get(0).scheduledTime());
        assertEquals("22:00", responses.get(1).scheduledTime());
    }

    @Test
    void updatesOwnedScheduleAndWritesAuditLog() {
        var user = buildUser(1L);
        var schedule = buildSchedule(101L, user, buildMedication(10L, user));
        when(medicationScheduleRepository.findById(101L)).thenReturn(Optional.of(schedule));
        doAnswer(invocation -> invocation.getArgument(0)).when(medicationScheduleRepository).save(any(MedicationSchedule.class));

        var response = scheduleService.updateSchedule(
            1L,
            101L,
            new UpdateScheduleRequest("21:30", "1.5")
        );

        assertEquals("21:30", response.scheduledTime());
        assertEquals("1.5", response.doseAmount());
        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals("UPDATE_SCHEDULE", auditCaptor.getValue().getAction());
        assertEquals(101L, auditCaptor.getValue().getTargetId());
    }

    @Test
    void rejectsUpdatingScheduleOwnedByAnotherUser() {
        var owner = buildUser(2L);
        var schedule = buildSchedule(101L, owner, buildMedication(10L, owner));
        when(medicationScheduleRepository.findById(101L)).thenReturn(Optional.of(schedule));

        assertThrows(
            ResponseStatusException.class,
            () -> scheduleService.updateSchedule(1L, 101L, new UpdateScheduleRequest("09:00", "1"))
        );
    }

    private static User buildUser(Long userId) {
        var user = new User();
        user.setUserId(userId);
        user.setFullName("Demo User");
        user.setEmail("demo@example.com");
        return user;
    }

    private static Medication buildMedication(Long medicationId, User user) {
        var medication = new Medication();
        medication.setMedicationId(medicationId);
        medication.setUser(user);
        medication.setMedicineName("Metformin");
        medication.setDosage("500mg");
        medication.setForm("Tablet");
        medication.setCurrentQuantity(20);
        medication.setRefillThreshold(5);
        return medication;
    }

    private static MedicationSchedule buildSchedule(Long scheduleId, User user, Medication medication) {
        var schedule = new MedicationSchedule();
        schedule.setScheduleId(scheduleId);
        schedule.setUser(user);
        schedule.setMedication(medication);
        schedule.setScheduledTime(LocalTime.of(8, 0));
        schedule.setDoseAmount(new BigDecimal("1"));
        schedule.setFrequency(ScheduleFrequency.DAILY);
        return schedule;
    }
}
