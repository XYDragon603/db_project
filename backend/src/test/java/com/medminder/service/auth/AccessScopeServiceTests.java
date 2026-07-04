package com.medminder.service.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.Role;
import com.medminder.domain.entity.User;
import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class AccessScopeServiceTests {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private RolePriorityResolver rolePriorityResolver;
    private AccessScopeService accessScopeService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userRoleRepository = Mockito.mock(UserRoleRepository.class);
        rolePriorityResolver = new RolePriorityResolver();
        accessScopeService = new AccessScopeService(userRepository, userRoleRepository, rolePriorityResolver);
    }

    @Test
    void allowsUserToAccessOwnUserScopedData() {
        var user = buildUser(1L, "emily@example.com");
        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("emily@example.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserUserId(1L)).thenReturn(List.of(buildUserRole(user, RoleName.USER)));

        assertDoesNotThrow(() -> accessScopeService.requireSelfAccess("emily@example.com", 1L));
    }

    @Test
    void resolvesAuthenticatedUserIdForUserScopedRequests() {
        var user = buildUser(12L, "new.user@example.com");
        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("new.user@example.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserUserId(12L)).thenReturn(List.of(buildUserRole(user, RoleName.USER)));

        assertEquals(12L, accessScopeService.resolveSelfUserId("new.user@example.com"));
    }

    @Test
    void rejectsUserTryingToAccessAnotherUsersData() {
        var user = buildUser(1L, "emily@example.com");
        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("emily@example.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserUserId(1L)).thenReturn(List.of(buildUserRole(user, RoleName.USER)));

        assertThrows(ResponseStatusException.class, () -> accessScopeService.requireSelfAccess("emily@example.com", 2L));
    }

    @Test
    void allowsCaregiverToAccessOwnDashboardScope() {
        var user = buildUser(6L, "alex.caregiver@example.com");
        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("alex.caregiver@example.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserUserId(6L)).thenReturn(List.of(buildUserRole(user, RoleName.CAREGIVER)));

        assertDoesNotThrow(() -> accessScopeService.requireCaregiverAccess("alex.caregiver@example.com", 6L));
    }

    @Test
    void rejectsAdminFromUserScopedMedicationApis() {
        var user = buildUser(8L, "admin@example.com");
        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("admin@example.com")).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUserUserId(8L)).thenReturn(List.of(buildUserRole(user, RoleName.ADMIN)));

        assertThrows(ResponseStatusException.class, () -> accessScopeService.requireSelfAccess("admin@example.com", 8L));
    }

    private static User buildUser(Long userId, String email) {
        var user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setActive(true);
        return user;
    }

    private static UserRole buildUserRole(User user, RoleName roleName) {
        var role = new Role();
        role.setRoleName(roleName);

        var userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        return userRole;
    }
}
