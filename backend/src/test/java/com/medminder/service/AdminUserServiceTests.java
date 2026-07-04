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
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import com.medminder.service.admin.AdminUserService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminUserServiceTests {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private AuditLogRepository auditLogRepository;
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userRoleRepository = Mockito.mock(UserRoleRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        adminUserService = new AdminUserService(userRepository, userRoleRepository, auditLogRepository);
    }

    @Test
    void adminCanListUsers() {
        var emily = buildUser(1L, "Emily Johnson", "emily@example.com", true);
        var alex = buildUser(6L, "Alex Johnson", "alex.caregiver@example.com", true);

        when(userRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(alex, emily));
        when(userRoleRepository.findByUserUserId(1L)).thenReturn(List.of(buildUserRole(emily, RoleName.USER)));
        when(userRoleRepository.findByUserUserId(6L)).thenReturn(List.of(buildUserRole(alex, RoleName.CAREGIVER)));

        var response = adminUserService.getUsers();

        assertEquals(2, response.size());
        assertEquals("Alex Johnson", response.getFirst().fullName());
        assertEquals(List.of("CAREGIVER"), response.getFirst().roles());
    }

    @Test
    void adminCanDeactivateUser() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);
        var target = buildUser(11L, "Test User", "test.user@example.com", true);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(11L)).thenReturn(Optional.of(target));
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

        var response = adminUserService.deactivateUser("admin@example.com", 11L);

        assertEquals(false, response.active());
        assertEquals(false, target.isActive());

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals("DEACTIVATE_USER", captor.getValue().getAction());
        assertEquals("users", captor.getValue().getTargetTable());
        assertEquals(11L, captor.getValue().getTargetId());
    }

    @Test
    void adminCanReactivateUser() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);
        var target = buildUser(11L, "Test User", "test.user@example.com", false);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(11L)).thenReturn(Optional.of(target));
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

        var response = adminUserService.reactivateUser("admin@example.com", 11L);

        assertEquals(true, response.active());
        assertEquals(true, target.isActive());

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals("REACTIVATE_USER", captor.getValue().getAction());
        assertEquals("users", captor.getValue().getTargetTable());
        assertEquals(11L, captor.getValue().getTargetId());
    }

    @Test
    void adminCannotDeactivateOwnAccount() {
        var admin = buildUser(8L, "Admin User", "admin@example.com", true);

        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(8L)).thenReturn(Optional.of(admin));

        var exception = assertThrows(
            ResponseStatusException.class,
            () -> adminUserService.deactivateUser("admin@example.com", 8L)
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

    private static UserRole buildUserRole(User user, RoleName roleName) {
        var role = new Role();
        role.setRoleName(roleName);

        var userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        return userRole;
    }
}
