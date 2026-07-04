package com.medminder.service.profile;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.dto.ProfileResponse;
import com.medminder.web.dto.UpdateProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public ProfileService(UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public ProfileResponse getProfile(Long userId) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var trimmedFullName = request.fullName().trim();
        if (trimmedFullName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Full name is required");
        }

        user.setFullName(trimmedFullName);
        user.setPhone(normalizePhone(request.phone()));
        var saved = userRepository.save(user);

        var auditLog = new AuditLog();
        auditLog.setUser(saved);
        auditLog.setAction("UPDATE_PROFILE");
        auditLog.setTargetTable("users");
        auditLog.setTargetId(saved.getUserId());
        auditLog.setDetails("Updated basic profile information");
        auditLogRepository.save(auditLog);

        return toResponse(saved);
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        var trimmed = phone.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private ProfileResponse toResponse(com.medminder.domain.entity.User user) {
        return new ProfileResponse(
            user.getUserId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.isActive(),
            user.getCreatedAt().toString()
        );
    }
}
