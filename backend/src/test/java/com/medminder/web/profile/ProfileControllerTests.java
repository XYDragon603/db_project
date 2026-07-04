package com.medminder.web.profile;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.profile.ProfileService;
import com.medminder.web.dto.ProfileResponse;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessScopeService accessScopeService;

    @MockBean
    private ProfileService profileService;

    @Test
    void userCanViewOwnProfile() throws Exception {
        when(accessScopeService.resolveSelfUserId("emily@example.com")).thenReturn(1L);
        when(profileService.getProfile(1L)).thenReturn(
            new ProfileResponse(1L, "Emily Johnson", "emily@example.com", "555-0101", true, "2026-07-01T08:30:00Z")
        );

        mockMvc.perform(get("/api/profile")
                .queryParam("userId", "1")
                .principal((Principal) () -> "emily@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Emily Johnson"))
            .andExpect(jsonPath("$.email").value("emily@example.com"));
    }

    @Test
    void userCanViewProfileWithoutPassingUserIdWhenIdentityIsAuthenticated() throws Exception {
        when(accessScopeService.resolveSelfUserId("emily@example.com")).thenReturn(1L);
        when(profileService.getProfile(1L)).thenReturn(
            new ProfileResponse(1L, "Emily Johnson", "emily@example.com", "555-0101", true, "2026-07-01T08:30:00Z")
        );

        mockMvc.perform(get("/api/profile")
                .principal((Principal) () -> "emily@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void userCannotViewAnotherUsersProfile() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own records"))
            .when(accessScopeService)
            .requireSelfAccess("daniel@example.com", 1L);

        mockMvc.perform(get("/api/profile")
                .queryParam("userId", "1")
                .principal((Principal) () -> "daniel@example.com"))
            .andExpect(status().isForbidden());
    }

    @Test
    void userCanUpdateOwnProfile() throws Exception {
        when(accessScopeService.resolveSelfUserId("emily@example.com")).thenReturn(1L);
        when(profileService.updateProfile(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
            .thenReturn(new ProfileResponse(1L, "Emily Carter", "emily@example.com", "555-0202", true, "2026-07-01T08:30:00Z"));

        mockMvc.perform(put("/api/profile")
                .queryParam("userId", "1")
                .principal((Principal) () -> "emily@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "fullName": "Emily Carter",
                      "phone": "555-0202"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName").value("Emily Carter"))
            .andExpect(jsonPath("$.phone").value("555-0202"));
    }

    @Test
    void userCannotUpdateAnotherUsersProfile() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own records"))
            .when(accessScopeService)
            .requireSelfAccess("daniel@example.com", 1L);

        mockMvc.perform(put("/api/profile")
                .queryParam("userId", "1")
                .principal((Principal) () -> "daniel@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "fullName": "Emily Carter",
                      "phone": "555-0202"
                    }
                    """))
            .andExpect(status().isForbidden());
    }
}
