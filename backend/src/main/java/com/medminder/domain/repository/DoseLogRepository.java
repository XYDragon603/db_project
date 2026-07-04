package com.medminder.domain.repository;

import com.medminder.domain.entity.DoseLog;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoseLogRepository extends JpaRepository<DoseLog, Long> {
    List<DoseLog> findByUserUserIdAndScheduledDatetimeBetweenOrderByScheduledDatetimeDesc(
        Long userId,
        OffsetDateTime start,
        OffsetDateTime end
    );
}
