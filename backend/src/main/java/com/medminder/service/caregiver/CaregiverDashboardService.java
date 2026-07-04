package com.medminder.service.caregiver;

import com.medminder.domain.enums.CaregiverAccessStatus;
import com.medminder.domain.enums.DoseStatus;
import com.medminder.domain.repository.CaregiverAccessRepository;
import com.medminder.domain.repository.DoseLogRepository;
import com.medminder.domain.repository.MedicationRepository;
import com.medminder.domain.repository.MedicationScheduleRepository;
import com.medminder.web.dto.CaregiverPatientOverviewResponse;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CaregiverDashboardService {

    private final CaregiverAccessRepository caregiverAccessRepository;
    private final DoseLogRepository doseLogRepository;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final MedicationRepository medicationRepository;

    public CaregiverDashboardService(
        CaregiverAccessRepository caregiverAccessRepository,
        DoseLogRepository doseLogRepository,
        MedicationScheduleRepository medicationScheduleRepository,
        MedicationRepository medicationRepository
    ) {
        this.caregiverAccessRepository = caregiverAccessRepository;
        this.doseLogRepository = doseLogRepository;
        this.medicationScheduleRepository = medicationScheduleRepository;
        this.medicationRepository = medicationRepository;
    }

    public List<CaregiverPatientOverviewResponse> getOverview(Long caregiverId) {
        var today = LocalDate.now(ZoneOffset.UTC);
        var start = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        var end = today.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        return caregiverAccessRepository.findByCaregiverUserIdAndAccessStatus(caregiverId, CaregiverAccessStatus.APPROVED).stream()
            .map(access -> {
                var userId = access.getUser().getUserId();
                var logs = doseLogRepository.findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(userId, start, end);
                var missed = logs.stream().filter(log -> log.getStatus() == DoseStatus.MISSED).count();
                var schedules = medicationScheduleRepository.findAll().stream()
                    .filter(schedule -> schedule.getUser().getUserId().equals(userId))
                    .filter(schedule -> schedule.isActive())
                    .count();
                var pending = Math.max(schedules - logs.size(), 0);
                var refillAlerts = medicationRepository.findByUserUserIdAndActiveTrueOrderByMedicineNameAsc(userId).stream()
                    .filter(medication -> medication.getCurrentQuantity() <= medication.getRefillThreshold())
                    .count();

                return new CaregiverPatientOverviewResponse(
                    userId,
                    access.getUser().getFullName(),
                    pending,
                    missed,
                    refillAlerts
                );
            })
            .toList();
    }
}
