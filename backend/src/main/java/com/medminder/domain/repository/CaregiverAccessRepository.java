package com.medminder.domain.repository;

import com.medminder.domain.entity.CaregiverAccess;
import com.medminder.domain.enums.CaregiverAccessStatus;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverAccessRepository extends JpaRepository<CaregiverAccess, Long> {
    List<CaregiverAccess> findByCaregiverUserIdAndAccessStatus(Long caregiverId, CaregiverAccessStatus accessStatus);
    List<CaregiverAccess> findByUserUserIdOrderByGrantedAtDesc(Long userId);
    Optional<CaregiverAccess> findByUserUserIdAndCaregiverUserId(Long userId, Long caregiverId);
}
