package com.medminder.service.admin;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.RoleRepository;
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
public class RoleManagementService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;

    public RoleManagementService(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        RoleRepository roleRepository,
        AuditLogRepository auditLogRepository
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<AdminUserResponse> getRoleUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AdminUserResponse assignRole(String adminEmail, Long userId, String roleNameValue) {
        var admin = requireActor(adminEmail);
        var target = requireUser(userId);
        var roleName = parseRoleName(roleNameValue);

        if (roleName != RoleName.CAREGIVER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CAREGIVER role can be assigned in this version");
        }
        if (userRoleRepository.existsByUserUserIdAndRoleRoleName(userId, roleName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That user already has the CAREGIVER role");
        }

        var role = roleRepository.findByRoleName(roleName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        var userRole = new UserRole();
        userRole.setUser(target);
        userRole.setRole(role);
        var saved = userRoleRepository.save(userRole);

        writeAuditLog(admin, "ASSIGN_ROLE", saved.getUserRoleId(), "Assigned CAREGIVER role to " + target.getEmail());
        return toResponse(target);
    }

    @Transactional
    public AdminUserResponse removeRole(String adminEmail, Long userId, String roleNameValue) {
        var admin = requireActor(adminEmail);
        var target = requireUser(userId);
        var roleName = parseRoleName(roleNameValue);

        if (roleName == RoleName.USER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The USER base role cannot be removed");
        }
        if (roleName == RoleName.ADMIN) {
            if (admin.getUserId().equals(target.getUserId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove your own ADMIN role");
            }
            if (target.isActive() && userRoleRepository.countByRoleRoleNameAndUserActiveTrue(RoleName.ADMIN) <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove the last active ADMIN role");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CAREGIVER role can be removed in this version");
        }
        if (roleName != RoleName.CAREGIVER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CAREGIVER role can be removed in this version");
        }

        var userRole = userRoleRepository.findByUserUserIdAndRoleRoleName(userId, roleName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "That user does not have the requested role"));

        var removedUserRoleId = userRole.getUserRoleId();
        userRoleRepository.delete(userRole);
        writeAuditLog(admin, "REMOVE_ROLE", removedUserRoleId, "Removed CAREGIVER role from " + target.getEmail());
        return toResponse(target);
    }

    private com.medminder.domain.entity.User requireActor(String adminEmail) {
        return userRepository.findByEmailIgnoreCase(adminEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private com.medminder.domain.entity.User requireUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private RoleName parseRoleName(String value) {
        try {
            return RoleName.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found");
        }
    }

    private void writeAuditLog(com.medminder.domain.entity.User actor, String action, Long targetId, String details) {
        var auditLog = new AuditLog();
        auditLog.setUser(actor);
        auditLog.setAction(action);
        auditLog.setTargetTable("user_roles");
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
