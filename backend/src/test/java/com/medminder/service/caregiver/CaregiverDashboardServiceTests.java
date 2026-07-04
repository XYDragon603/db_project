package com.medminder.service.caregiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.CaregiverAccess;
import com.medminder.domain.entity.User;
import com.medminder.domain.enums.CaregiverAccessStatus;
import com.medminder.domain.repository.CaregiverAccessRepository;
import com.medminder.domain.repository.DoseLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CaregiverDashboardServiceTests {

    private CaregiverAccessRepository caregiverAccessRepository;
    private DoseLogRepository doseLogRepository;
    private MedicationScheduleRepository medicationScheduleRepository;
    private MedicationRepository medicationRepository;
    private CaregiverDashboardService caregiverDashboardService;

    @BeforeEach
    void setUp() {
        caregiverAccessRepository = Mockito.mock(CaregiverAccessRepository.class);
        doseLogRepository = Mockito.mock(DoseLogRepository.class);
        medicationScheduleRepository = Mockito.mock(MedicationScheduleRepository.class);
        medicationRepository = Mockito.mock(MedicationRepository.class);
        caregiverDashboardService = new CaregiverDashboardService(
            caregiverAccessRepository,
            doseLogRepository,
            medicationScheduleRepository,
            medicationRepository
        );
    }

    @Test
    void includesOnlyApprovedAccessRecordsInDashboardOverview() {
        var patient = buildUser(1L, "Emily Johnson");
        var caregiver = buildUser(6L, "Alex Johnson");
        var approved = buildAccess(601L, patient, caregiver, CaregiverAccessStatus.APPROVED);

        when(caregiverAccessRepository.findByCaregiverUserIdAndAccessStatus(6L, CaregiverAccessStatus.APPROVED))
            .thenReturn(List.of(approved));
        when(doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(
            Mockito.eq(1L),
            Mockito.any(),
            Mockito.any()
        )).thenReturn(List.of());
        when(medicationScheduleRepository.findAll()).thenReturn(List.of());
        when(medicationRepository.findByUserUserIdAndActiveTrueOrderByMedicineNameAsc(1L)).thenReturn(List.of());

        var response = caregiverDashboardService.getOverview(6L);

        assertEquals(1, response.size());
        assertEquals(1L, response.getFirst().patientUserId());
        assertEquals("Emily Johnson", response.getFirst().patientName());
    }

    private static User buildUser(Long userId, String fullName) {
        var user = new User();
        user.setUserId(userId);
        user.setFullName(fullName);
        user.setActive(true);
        return user;
    }

    private static CaregiverAccess buildAccess(
        Long accessId,
        User user,
        User caregiver,
        CaregiverAccessStatus status
    ) {
        var access = new CaregiverAccess();
        access.setAccessId(accessId);
        access.setUser(user);
        access.setCaregiver(caregiver);
        access.setAccessStatus(status);
        return access;
    }
}
