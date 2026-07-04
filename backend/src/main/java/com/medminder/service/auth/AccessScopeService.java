package com.medminder.service.auth;

import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccessScopeService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePriorityResolver rolePriorityResolver;

    public AccessScopeService(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        RolePriorityResolver rolePriorityResolver
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePriorityResolver = rolePriorityResolver;
    }

    public void requireSelfAccess(String authenticatedEmail, Long requestedUserId) {
        var authenticatedUserId = resolveSelfUserId(authenticatedEmail);
        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own records");
        }
    }

    public Long resolveSelfUserId(String authenticatedEmail) {
        var context = getContext(authenticatedEmail);
        if (context.roleName() != RoleName.USER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User-scoped endpoint");
        }
        return context.userId();
    }

    public void requireCaregiverAccess(String authenticatedEmail, Long requestedCaregiverId) {
        var context = getContext(authenticatedEmail);
        if (context.roleName() != RoleName.CAREGIVER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Caregiver-scoped endpoint");
        }
        if (!context.userId().equals(requestedCaregiverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own caregiver dashboard");
        }
    }

    private UserAccessContext getContext(String authenticatedEmail) {
        var user = userRepository.findByEmailIgnoreCaseAndActiveTrue(authenticatedEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

        var primaryRole = rolePriorityResolver.resolvePrimaryRole(userRoleRepository.findByUserUserId(user.getUserId()));

        return new UserAccessContext(user.getUserId(), primaryRole);
    }

    private record UserAccessContext(Long userId, RoleName roleName) {}
}
