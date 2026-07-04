package com.medminder.service.caregiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.CaregiverAccess;
import com.medminder.domain.entity.Role;
import com.medminder.domain.entity.User;
import com.medminder.domain.entity.UserRole;
import com.medminder.domain.enums.CaregiverAccessStatus;
import com.medminder.domain.enums.RoleName;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.CaregiverAccessRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.domain.repository.UserRoleRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

class CaregiverAccessServiceTests {

    private CaregiverAccessRepository caregiverAccessRepository;
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private AuditLogRepository auditLogRepository;
    private CaregiverAccessService caregiverAccessService;

    @BeforeEach
    void setUp() {
        caregiverAccessRepository = Mockito.mock(CaregiverAccessRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        userRoleRepository = Mockito.mock(UserRoleRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        caregiverAccessService = new CaregiverAccessService(
            caregiverAccessRepository,
            userRepository,
            userRoleRepository,
            auditLogRepository
        );
    }

    @Test
    void grantsCaregiverAccessToValidCaregiver() {
      var user = buildUser(1L, "emily@example.com", "Emily Johnson");
      var caregiver = buildUser(6L, "alex.caregiver@example.com", "Alex Johnson");

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.findByEmailIgnoreCaseAndActiveTrue("alex.caregiver@example.com")).thenReturn(Optional.of(caregiver));
      when(userRoleRepository.findByUserUserId(6L)).thenReturn(List.of(buildUserRole(caregiver, RoleName.CAREGIVER)));
      when(caregiverAccessRepository.findByUserUserIdOrderByGrantedAtDesc(1L)).thenReturn(List.of());
      doAnswer(invocation -> invocation.getArgument(0)).when(caregiverAccessRepository).save(any(CaregiverAccess.class));

      var response = caregiverAccessService.grantAccess(1L, "alex.caregiver@example.com");

      assertEquals(CaregiverAccessStatus.APPROVED.name(), response.accessStatus());
      assertEquals("alex.caregiver@example.com", response.caregiverEmail());
      verify(auditLogRepository).save(any());
    }

    @Test
    void rejectsGrantToNonCaregiver() {
      var user = buildUser(1L, "emily@example.com", "Emily Johnson");
      var nonCaregiver = buildUser(2L, "daniel@example.com", "Daniel Lee");

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.findByEmailIgnoreCaseAndActiveTrue("daniel@example.com")).thenReturn(Optional.of(nonCaregiver));
      when(userRoleRepository.findByUserUserId(2L)).thenReturn(List.of(buildUserRole(nonCaregiver, RoleName.USER)));

      assertThrows(ResponseStatusException.class, () -> caregiverAccessService.grantAccess(1L, "daniel@example.com"));
    }

    @Test
    void reactivatesRevokedCaregiverAccess() {
      var user = buildUser(1L, "emily@example.com", "Emily Johnson");
      var caregiver = buildUser(6L, "alex.caregiver@example.com", "Alex Johnson");
      var existing = buildAccess(601L, user, caregiver, CaregiverAccessStatus.REVOKED);

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.findByEmailIgnoreCaseAndActiveTrue("alex.caregiver@example.com")).thenReturn(Optional.of(caregiver));
      when(userRoleRepository.findByUserUserId(6L)).thenReturn(List.of(buildUserRole(caregiver, RoleName.CAREGIVER)));
      when(caregiverAccessRepository.findByUserUserIdAndCaregiverUserId(1L, 6L)).thenReturn(Optional.of(existing));
      doAnswer(invocation -> invocation.getArgument(0)).when(caregiverAccessRepository).save(any(CaregiverAccess.class));

      var response = caregiverAccessService.grantAccess(1L, "alex.caregiver@example.com");

      assertEquals(CaregiverAccessStatus.APPROVED.name(), response.accessStatus());
      assertEquals(601L, response.accessId());
    }

    @Test
    void rejectsGrantToSelf() {
      var user = buildUser(1L, "emily@example.com", "Emily Johnson");

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userRepository.findByEmailIgnoreCaseAndActiveTrue("emily@example.com")).thenReturn(Optional.of(user));

      assertThrows(ResponseStatusException.class, () -> caregiverAccessService.grantAccess(1L, "emily@example.com"));
    }

    @Test
    void rejectsManagingAnotherUsersAccess() {
      var owner = buildUser(1L, "emily@example.com", "Emily Johnson");
      var caregiver = buildUser(6L, "alex.caregiver@example.com", "Alex Johnson");
      var record = buildAccess(601L, owner, caregiver, CaregiverAccessStatus.APPROVED);
      var actor = buildUser(2L, "daniel@example.com", "Daniel Lee");

      when(userRepository.findById(2L)).thenReturn(Optional.of(actor));
      when(caregiverAccessRepository.findById(601L)).thenReturn(Optional.of(record));

      assertThrows(ResponseStatusException.class, () -> caregiverAccessService.revokeAccess(2L, 601L));
    }

    @Test
    void revokeChangesStatusToRevoked() {
      var owner = buildUser(1L, "emily@example.com", "Emily Johnson");
      var caregiver = buildUser(6L, "alex.caregiver@example.com", "Alex Johnson");
      var record = buildAccess(601L, owner, caregiver, CaregiverAccessStatus.APPROVED);

      when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
      when(caregiverAccessRepository.findById(601L)).thenReturn(Optional.of(record));
      doAnswer(invocation -> invocation.getArgument(0)).when(caregiverAccessRepository).save(any(CaregiverAccess.class));

      var response = caregiverAccessService.revokeAccess(1L, 601L);

      assertEquals(CaregiverAccessStatus.REVOKED.name(), response.accessStatus());
    }

    private static User buildUser(Long userId, String email, String fullName) {
      var user = new User();
      user.setUserId(userId);
      user.setEmail(email);
      user.setFullName(fullName);
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

    private static CaregiverAccess buildAccess(
      Long accessId,
      User user,
      User caregiver,
      CaregiverAccessStatus status
    ) {
      var access = new CaregiverAccess();
      access.setAccessId(accessId);
      access.setUser(user);
      access.setCaregiver(caregiver);
      access.setAccessStatus(status);
      return access;
    }
}
