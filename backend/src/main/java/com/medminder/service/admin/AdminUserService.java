package com.medminder.service.admin;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import com.medminder.web.dto.AdminUserResponse;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuditLogRepository auditLogRepository;

    public AdminUserService(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        AuditLogRepository auditLogRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<AdminUserResponse> getUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AdminUserResponse deactivateUser(String adminEmail, Long userId) {
        var admin = requireActor(adminEmail);
        var target = requireUser(userId);

        if (admin.getUserId().equals(target.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot deactivate your own admin account");
        }

        target.setActive(false);
        var saved = userRepository.save(target);
        writeAuditLog(admin, "DEACTIVATE_USER", saved.getUserId(), "Deactivated user account " + saved.getEmail());
        return toResponse(saved);
    }

    @Transactional
    public AdminUserResponse reactivateUser(String adminEmail, Long userId) {
        var admin = requireActor(adminEmail);
        var target = requireUser(userId);

        target.setActive(true);
        var saved = userRepository.save(target);
        writeAuditLog(admin, "REACTIVATE_USER", saved.getUserId(), "Reactivated user account " + saved.getEmail());
        return toResponse(saved);
    }

    private com.medminder.domain.entity.User requireActor(String adminEmail) {
        return userRepository.findByEmailIgnoreCase(adminEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private com.medminder.domain.entity.User requireUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void writeAuditLog(com.medminder.domain.entity.User actor, String action, Long targetId, String details) {
        var auditLog = new AuditLog();
        auditLog.setUser(actor);
        auditLog.setAction(action);
        auditLog.setTargetTable("users");
        auditLog.setTargetId(targetId);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    private AdminUserResponse toResponse(com.medminder.domain.entity.User user) {
        var roles = userRoleRepository.findByUserUserId(user.getUserId()).stream()
            .map(userRole -> userRole.getRole().getRoleName().name())
            .sorted(Comparator.naturalOrder())
            .toList();

        return new AdminUserResponse(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.isActive(),
            roles,
            user.getCreatedAt().toString()
        );
    }
}
