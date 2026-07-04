package com.medminder.service.auth;

import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
public class RolePriorityResolver {

    public RoleName resolvePrimaryRole(List<UserRole> userRoles) {
        return userRoles.stream()
            .map(userRole -> userRole.getRole().getRoleName())
            .min(Comparator.comparingInt(this::priorityOf))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No role assigned"));
    }

    private int priorityOf(RoleName roleName) {
        return switch (roleName) {
            case ADMIN -> 0;
            case CAREGIVER -> 1;
            case USER -> 2;
        };
    }
}
