package com.medminder.domain.repository;

import com.medminder.domain.entity.Medication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByUserUserIdAndActiveTrueOrderByMedicineNameAsc(Long userId);
    List<Medication> findByUserUserIdAndActiveTrueAndCurrentQuantityLessThanEqualOrderByCurrentQuantityAsc(
        Long userId,
        int threshold
    );
}
