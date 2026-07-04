package com.medminder.service.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medminder.domain.entity.AuditLog;
import com.medminder.domain.entity.User;
import com.medminder.domain.repository.AuditLogRepository;
import com.medminder.domain.repository.UserRepository;
import com.medminder.web.dto.UpdateProfileRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class ProfileServiceTests {

    private UserRepository userRepository;
    private AuditLogRepository auditLogRepository;
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        auditLogRepository = Mockito.mock(AuditLogRepository.class);
        profileService = new ProfileService(userRepository, auditLogRepository);
    }

    @Test
    void returnsProfileForExistingUser() {
        var user = buildUser(1L, "Emily Johnson", "emily@example.com", "555-0101");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var response = profileService.getProfile(1L);

        assertEquals(1L, response.userId());
        assertEquals("Emily Johnson", response.fullName());
        assertEquals("emily@example.com", response.email());
        assertEquals("555-0101", response.phone());
        assertEquals("2026-07-01T08:30Z", response.createdAt());
    }

    @Test
    void updatesOwnFullNameAndPhone() {
        var user = buildUser(1L, "Emily Johnson", "emily@example.com", "555-0101");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

        var response = profileService.updateProfile(1L, new UpdateProfileRequest("  Emily Carter  ", " 555-0202 "));

        assertEquals("Emily Carter", response.fullName());
        assertEquals("555-0202", response.phone());
        assertEquals("Emily Carter", user.getFullName());
        assertEquals("555-0202", user.getPhone());
    }

    @Test
    void updateWritesAuditLog() {
        var user = buildUser(1L, "Emily Johnson", "emily@example.com", "555-0101");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

        profileService.updateProfile(1L, new UpdateProfileRequest("Emily Carter", "555-0202"));

        var captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals("UPDATE_PROFILE", captor.getValue().getAction());
        assertEquals("users", captor.getValue().getTargetTable());
        assertEquals(1L, captor.getValue().getTargetId());
    }

    private static User buildUser(Long userId, String fullName, String email, String phone) {
        var user = new User();
        user.setUserId(userId);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setActive(true);
        user.setCreatedAt(OffsetDateTime.parse("2026-07-01T08:30:00Z"));
        return user;
    }
}
