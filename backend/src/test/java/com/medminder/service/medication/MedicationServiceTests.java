package com.medminder.service.medication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.Medication;
import com.medminder.domain.entity.User;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.dto.CreateMedicationRequest;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class MedicationServiceTests {

    private MedicationRepository medicationRepository;
    private UserRepository userRepository;
    private AuditLogRepository auditLogRepository;
    private MedicationService medicationService;

    @BeforeEach
    void setUp() {
        medicationRepository = Mockito.mock(MedicationRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        medicationService = new MedicationService(medicationRepository, userRepository, auditLogRepository);
    }

    @Test
    void updatesOwnedMedicationAndWritesAuditLog() {
        var user = buildUser(1L);
        var medication = buildMedication(101L, user, "Metformin", "500mg");
        var request = new CreateMedicationRequest(
            "Metformin XR",
            "750mg",
            "Tablet",
            22,
            6,
            "2026-07-01",
            "2026-12-31",
            "Updated note"
        );

        when(medicationRepository.findById(101L)).thenReturn(Optional.of(medication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> invocation.getArgument(0)).when(medicationRepository).save(any(Medication.class));

        var response = medicationService.updateMedication(1L, 101L, request);

        assertEquals("Metformin XR", response.medicineName());
        assertEquals("750mg", response.dosage());
        assertEquals(22, response.currentQuantity());
        assertEquals(LocalDate.parse("2026-12-31"), medication.getEndDate());

        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals("UPDATE_MEDICATION", auditCaptor.getValue().getAction());
        assertEquals("medications", auditCaptor.getValue().getTargetTable());
        assertEquals(101L, auditCaptor.getValue().getTargetId());
    }

    @Test
    void rejectsUpdatingMedicationOwnedByAnotherUser() {
        var owner = buildUser(2L);
        var medication = buildMedication(101L, owner, "Metformin", "500mg");
        var request = new CreateMedicationRequest(
            "Metformin",
            "500mg",
            "Tablet",
            20,
            5,
            "2026-07-01",
            null,
            "Note"
        );

        when(medicationRepository.findById(101L)).thenReturn(Optional.of(medication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));

        assertThrows(ResponseStatusException.class, () -> medicationService.updateMedication(1L, 101L, request));
    }

    @Test
    void deactivatesOwnedMedicationAndWritesAuditLog() {
        var user = buildUser(1L);
        var medication = buildMedication(101L, user, "Metformin", "500mg");

        when(medicationRepository.findById(101L)).thenReturn(Optional.of(medication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> invocation.getArgument(0)).when(medicationRepository).save(any(Medication.class));

        var response = medicationService.deactivateMedication(1L, 101L);

        assertFalse(response.active());
        assertFalse(medication.isActive());

        ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditCaptor.capture());
        assertEquals("DEACTIVATE_MEDICATION", auditCaptor.getValue().getAction());
        assertEquals("medications", auditCaptor.getValue().getTargetTable());
        assertEquals(101L, auditCaptor.getValue().getTargetId());
    }

    @Test
    void rejectsDeactivatingMedicationOwnedByAnotherUser() {
        var owner = buildUser(2L);
        var medication = buildMedication(101L, owner, "Metformin", "500mg");

        when(medicationRepository.findById(101L)).thenReturn(Optional.of(medication));
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));

        assertThrows(ResponseStatusException.class, () -> medicationService.deactivateMedication(1L, 101L));
    }

    private static User buildUser(Long userId) {
        var user = new User();
        user.setUserId(userId);
        user.setFullName("Demo User");
        user.setEmail("demo@example.com");
        return user;
    }

    private static Medication buildMedication(Long medicationId, User user, String medicineName, String dosage) {
        var medication = new Medication();
        medication.setMedicationId(medicationId);
        medication.setUser(user);
        medication.setMedicineName(medicineName);
        medication.setDosage(dosage);
        medication.setForm("Tablet");
        medication.setCurrentQuantity(20);
        medication.setRefillThreshold(5);
        return medication;
    }
}
