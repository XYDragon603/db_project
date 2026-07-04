package com.medminder.domain.repository;

import com.medminder.domain.entity.MedicationSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long> {
    List<MedicationSchedule> findByUserUserIdAndActiveTrue(Long userId);
    List<MedicationSchedule> findByUserUserIdAndMedicationMedicationIdOrderByScheduledTimeAsc(Long userId, Long medicationId);
    List<MedicationSchedule> findByUserUserIdAndMedicationMedicationIdAndActiveTrueOrderByScheduledTimeAsc(Long userId, Long medicationId);
}
