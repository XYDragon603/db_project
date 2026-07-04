package com.medminder.domain.repository;

import com.medminder.domain.entity.RefillRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefillRecordRepository extends JpaRepository<RefillRecord, Long> {
    List<RefillRecord> findTop10ByUserUserIdOrderByRefillDateDesc(Long userId);
}
