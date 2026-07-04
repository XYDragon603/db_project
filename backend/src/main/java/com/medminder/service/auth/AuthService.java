package com.medminder.service.auth;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.User;
import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.RoleRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import com.medminder.web.dto.AuthLoginRequest;
import com.medminder.web.dto.AuthLoginResponse;
import com.medminder.web.dto.AuthRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolePriorityResolver rolePriorityResolver;

    public AuthService(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        RoleRepository roleRepository,
        AuditLogRepository auditLogRepository,
        PasswordEncoder passwordEncoder,
        RolePriorityResolver rolePriorityResolver
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolePriorityResolver = rolePriorityResolver;
    }

    public AuthLoginResponse login(AuthLoginRequest request) {
        var normalizedEmail = request.email().trim().toLowerCase();
        var user = userRepository.findByEmailIgnoreCaseAndActiveTrue(normalizedEmail)
            .orElseGet(() -> userRepository.findByEmailIgnoreCase(normalizedEmail)
                .map(existing -> {
                    if (!existing.isActive()) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to sign in with that account");
                    }
                    return existing;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials")));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        var primaryRole = rolePriorityResolver.resolvePrimaryRole(userRoleRepository.findByUserUserId(user.getUserId()));

        return new AuthLoginResponse(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            primaryRole.name()
        );
    }

    @Transactional
    public AuthLoginResponse register(AuthRegisterRequest request) {
        var normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        var userRole = roleRepository.findByRoleName(RoleName.USER)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "USER role is missing"));

        var user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
        var savedUser = userRepository.save(user);

        var roleAssignment = new UserRole();
        roleAssignment.setUser(savedUser);
        roleAssignment.setRole(userRole);
        userRoleRepository.save(roleAssignment);

        var auditLog = new AuditLog();
        auditLog.setUser(savedUser);
        auditLog.setAction("REGISTER_USER");
        auditLog.setTargetTable("users");
        auditLog.setTargetId(savedUser.getUserId());
        auditLog.setDetails("Registered new user account");
        auditLogRepository.save(auditLog);

        return new AuthLoginResponse(
            savedUser.getUserId(),
            savedUser.getFullName(),
            savedUser.getEmail(),
            RoleName.USER.name()
        );
    }
}
