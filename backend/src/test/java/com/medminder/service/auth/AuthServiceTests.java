package com.medminder.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.Role;
import com.medminder.domain.entity.User;
import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.RoleRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import com.medminder.web.dto.AuthRegisterRequest;
import com.medminder.web.dto.AuthLoginRequest;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTests {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private RoleRepository roleRepository;
    private AuditLogRepository auditLogRepository;
    private PasswordEncoder passwordEncoder;
    private RolePriorityResolver rolePriorityResolver;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userRoleRepository = Mockito.mock(UserRoleRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        rolePriorityResolver = new RolePriorityResolver();
        authService = new AuthService(
            userRepository,
            userRoleRepository,
            roleRepository,
            auditLogRepository,
            passwordEncoder,
            rolePriorityResolver
        );
    }

    @Test
    void registersNewUserWithUserRole() {
        var role = new Role();
        role.setRoleId(1L);
        role.setRoleName(RoleName.USER);

        when(userRepository.existsByEmailIgnoreCase("new.user@example.com")).thenReturn(false);
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        doAnswer(invocation -> {
            var user = invocation.getArgument(0, User.class);
            user.setUserId(11L);
            return user;
        }).when(userRepository).save(any(User.class));

        var response = authService.register(new AuthRegisterRequest(
            "New User",
            "new.user@example.com",
            "password123",
            "555-0199"
        ));

        assertEquals(11L, response.userId());
        assertEquals("USER", response.role());
        assertEquals("new.user@example.com", response.email());
        verify(userRoleRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    void rejectsDuplicateEmailRegistration() {
        when(userRepository.existsByEmailIgnoreCase("emily@example.com")).thenReturn(true);

        var exception = assertThrows(ResponseStatusException.class, () -> authService.register(new AuthRegisterRequest(
            "Emily Johnson",
            "emily@example.com",
            "password123",
            null
        )));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void deactivatedUserCannotLogIn() {
        var user = new User();
        user.setUserId(12L);
        user.setFullName("Inactive User");
        user.setEmail("inactive@example.com");
        user.setPasswordHash("hashed-password");
        user.setActive(false);

        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("inactive@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmailIgnoreCase("inactive@example.com")).thenReturn(Optional.of(user));

        var exception = assertThrows(
            ResponseStatusException.class,
            () -> authService.login(new AuthLoginRequest("inactive@example.com", "password"))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void loginPrefersCaregiverRoleWhenUserHasUserAndCaregiver() {
        var user = new User();
        user.setUserId(12L);
        user.setFullName("Role Test User");
        user.setEmail("role.test@example.com");
        user.setPasswordHash("hashed-password");
        user.setActive(true);

        when(userRepository.findByEmailIgnoreCaseAndActiveTrue("role.test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(userRoleRepository.findByUserUserId(12L)).thenReturn(List.of(
            buildUserRole(user, RoleName.USER),
            buildUserRole(user, RoleName.CAREGIVER)
        ));

        var response = authService.login(new AuthLoginRequest("role.test@example.com", "password123"));

        assertEquals("CAREGIVER", response.role());
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
