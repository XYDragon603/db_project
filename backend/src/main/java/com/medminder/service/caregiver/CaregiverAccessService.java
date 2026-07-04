package com.medminder.service.caregiver;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.CaregiverAccess;
import com.medminder.domain.entity.User;
import com.medminder.domain.enums.CaregiverAccessStatus;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.CaregiverAccessRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import com.medminder.web.dto.CaregiverAccessResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaregiverAccessService {

    private final CaregiverAccessRepository caregiverAccessRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuditLogRepository auditLogRepository;

    public CaregiverAccessService(
        CaregiverAccessRepository caregiverAccessRepository,
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        AuditLogRepository auditLogRepository
    ) {
        this.caregiverAccessRepository = caregiverAccessRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<CaregiverAccessResponse> getAccessRecords(Long userId) {
        requireUser(userId);
        return caregiverAccessRepository.findByUserUserIdOrderByGrantedAtDesc(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public CaregiverAccessResponse grantAccess(Long userId, String caregiverEmail) {
        var user = requireUser(userId);
        var normalizedEmail = caregiverEmail.trim().toLowerCase();
        var caregiver = userRepository.findByEmailIgnoreCaseAndActiveTrue(normalizedEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caregiver account not found"));

        if (user.getUserId().equals(caregiver.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot grant access to yourself");
        }

        if (!hasRole(caregiver.getUserId(), RoleName.CAREGIVER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected account is not a caregiver");
        }

        var access = caregiverAccessRepository.findByUserUserIdAndCaregiverUserId(userId, caregiver.getUserId())
            .orElseGet(CaregiverAccess::new);
        access.setUser(user);
        access.setCaregiver(caregiver);
        access.setAccessStatus(CaregiverAccessStatus.APPROVED);
        access.setGrantedAt(OffsetDateTime.now());

        var saved = caregiverAccessRepository.save(access);
        auditLogRepository.save(buildAuditLog(
            user,
            "GRANT_CAREGIVER_ACCESS",
            saved.getAccessId(),
            "Granted caregiver access to " + caregiver.getEmail()
        ));

        return toResponse(saved);
    }

    @Transactional
    public CaregiverAccessResponse revokeAccess(Long userId, Long accessId) {
        var user = requireUser(userId);
        var access = caregiverAccessRepository.findById(accessId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caregiver access record not found"));

        if (!access.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own caregiver access");
        }

        access.setAccessStatus(CaregiverAccessStatus.REVOKED);
        var saved = caregiverAccessRepository.save(access);
        auditLogRepository.save(buildAuditLog(
            user,
            "REVOKE_CAREGIVER_ACCESS",
            saved.getAccessId(),
            "Revoked caregiver access for " + saved.getCaregiver().getEmail()
        ));

        return toResponse(saved);
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private boolean hasRole(Long userId, RoleName roleName) {
        return userRoleRepository.findByUserUserId(userId).stream()
            .anyMatch(userRole -> userRole.getRole().getRoleName() == roleName);
    }

    private AuditLog buildAuditLog(User user, String action, Long targetId, String details) {
        var auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setTargetTable("caregiver_access");
        auditLog.setTargetId(targetId);
        auditLog.setDetails(details);
        return auditLog;
    }

    private CaregiverAccessResponse toResponse(CaregiverAccess access) {
        return new CaregiverAccessResponse(
            access.getAccessId(),
            access.getCaregiver().getUserId(),
            access.getCaregiver().getFullName(),
            access.getCaregiver().getEmail(),
            access.getAccessStatus().name(),
            access.getGrantedAt().toString()
        );
    }
}
