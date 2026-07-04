package com.medminder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.Role;
import com.medminder.domain.entity.User;
import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.RoleRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import com.medminder.service.admin.RoleManagementService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class RoleManagementServiceTests {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private RoleRepository roleRepository;
    private AuditLogRepository auditLogRepository;
    private RoleManagementService roleManagementService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userRoleRepository = Mockito.mock(UserRoleRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        roleManagementService = new RoleManagementService(
            userRepository,
            userRoleRepository,
            roleRepository,
            auditLogRepository
        );
    }

    @Test
    void adminCanViewRoleManagementUserList() {
        var user = buildUser(1L, "Emily Johnson", "emily@example.com", true);
        when(userRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(user));
        when(userRoleRepository.findByUserUserId(1L)).thenReturn(List.of(buildUserRole(91L, user, RoleName.USER)));

        var response = roleManagementService.getRoleUsers();

        assertEquals(1, response.size());
        assertEquals(List.of("USER"), response.getFirst().roles());
    }

    @Test
    void adminCanAssignCaregiverRoleToUser() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);
        var target = buildUser(12L, "Role Test User", "role.test@example.com", true);
        var caregiverRole = buildRole(2L, RoleName.CAREGIVER);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(12L)).thenReturn(Optional.of(target));
        when(roleRepository.findByRoleName(RoleName.CAREGIVER)).thenReturn(Optional.of(caregiverRole));
        when(userRoleRepository.existsByUserUserIdAndRoleRoleName(12L, RoleName.CAREGIVER)).thenReturn(false);
        doAnswer(invocation -> {
            var userRole = invocation.getArgument(0, UserRole.class);
            userRole.setUserRoleId(501L);
            return userRole;
        }).when(userRoleRepository).save(any(UserRole.class));
        when(userRoleRepository.findByUserUserId(12L)).thenReturn(List.of(
            buildUserRole(100L, target, RoleName.USER),
            buildUserRole(501L, target, RoleName.CAREGIVER)
        ));

        var response = roleManagementService.assignRole("admin@example.com", 12L, "CAREGIVER");

        assertEquals(List.of("CAREGIVER", "USER"), response.roles());

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals("ASSIGN_ROLE", captor.getValue().getAction());
        assertEquals("user_roles", captor.getValue().getTargetTable());
        assertEquals(501L, captor.getValue().getTargetId());
    }

    @Test
    void adminCannotAssignDuplicateCaregiverRole() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);
        var target = buildUser(12L, "Role Test User", "role.test@example.com", true);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(12L)).thenReturn(Optional.of(target));
        when(userRoleRepository.existsByUserUserIdAndRoleRoleName(12L, RoleName.CAREGIVER)).thenReturn(true);

        var exception = assertThrows(
            ResponseStatusException.class,
            () -> roleManagementService.assignRole("admin@example.com", 12L, "CAREGIVER")
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void adminCanRemoveCaregiverRole() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);
        var target = buildUser(12L, "Role Test User", "role.test@example.com", true);
        var caregiverUserRole = buildUserRole(501L, target, RoleName.CAREGIVER);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(12L)).thenReturn(Optional.of(target));
        when(userRoleRepository.findByUserUserIdAndRoleRoleName(12L, RoleName.CAREGIVER)).thenReturn(Optional.of(caregiverUserRole));
        when(userRoleRepository.findByUserUserId(12L)).thenReturn(List.of(buildUserRole(100L, target, RoleName.USER)));

        var response = roleManagementService.removeRole("admin@example.com", 12L, "CAREGIVER");

        verify(userRoleRepository).delete(caregiverUserRole);
        assertEquals(List.of("USER"), response.roles());

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals("REMOVE_ROLE", captor.getValue().getAction());
        assertEquals("user_roles", captor.getValue().getTargetTable());
        assertEquals(501L, captor.getValue().getTargetId());
    }

    @Test
    void adminCannotRemoveUserBaseRole() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);
        var target = buildUser(12L, "Role Test User", "role.test@example.com", true);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(12L)).thenReturn(Optional.of(target));

        var exception = assertThrows(
            ResponseStatusException.class,
            () -> roleManagementService.removeRole("admin@example.com", 12L, "USER")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void adminCannotRemoveOwnAdminRole() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(8L)).thenReturn(Optional.of(admin));

        var exception = assertThrows(
            ResponseStatusException.class,
            () -> roleManagementService.removeRole("admin@example.com", 8L, "ADMIN")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    private static User buildUser(Long userId, String fullName, String email, boolean active) {
        var user = new User();
        user.setUserId(userId);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone("555-0100");
        user.setActive(active);
        user.setCreatedAt(OffsetDateTime.parse("2026-07-01T08:30:00Z"));
        return user;
    }

    private static Role buildRole(Long roleId, RoleName roleName) {
        var role = new Role();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        return role;
    }

    private static UserRole buildUserRole(Long userRoleId, User user, RoleName roleName) {
        var role = new Role();
        role.setRoleName(roleName);

        var userRole = new UserRole();
        userRole.setUserRoleId(userRoleId);
        userRole.setUser(user);
        userRole.setRole(role);
        return userRole;
    }
}
