package com.medminder.service.admin;

import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.web.dto.AuditLogResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminAuditService {

    private final AuditLogRepository auditLogRepository;

    public AdminAuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogResponse> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc().stream()
            .map(log -> new AuditLogResponse(
                log.getAuditId(),
                log.getCreatedAt().toString(),
                log.getUser().getFullName(),
                log.getAction(),
                log.getTargetTable(),
                log.getTargetId(),
                log.getDetails()
            ))
            .toList();
    }
}
