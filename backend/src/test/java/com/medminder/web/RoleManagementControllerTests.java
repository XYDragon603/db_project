package com.medminder.web;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medminder.config.DemoUserDetailsService;
import com.medminder.service.admin.RoleManagementService;
import com.medminder.web.dto.AdminUserResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RoleManagementControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleManagementService roleManagementService;

    @MockBean
    private DemoUserDetailsService demoUserDetailsService;

    @Test
    void adminCanViewRoleManagementList() throws Exception {
        mockAdminUser("admin@example.com");
        when(roleManagementService.getRoleUsers()).thenReturn(List.of(
            new AdminUserResponse(12L, "Role Test User", "role.test@example.com", "555-0199", true, List.of("USER"), "2026-07-01T08:30:00Z")
        ));

        mockMvc.perform(get("/api/admin/roles/users").with(httpBasic("admin@example.com", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].roles[0]").value("USER"));
    }

    @Test
    void nonAdminCannotAccessRoleManagement() throws Exception {
        mockUser("emily@example.com");

        mockMvc.perform(get("/api/admin/roles/users").with(httpBasic("emily@example.com", "password")))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAssignCaregiverRole() throws Exception {
        mockAdminUser("admin@example.com");
        when(roleManagementService.assignRole("admin@example.com", 12L, "CAREGIVER")).thenReturn(
            new AdminUserResponse(12L, "Role Test User", "role.test@example.com", "555-0199", true, List.of("CAREGIVER", "USER"), "2026-07-01T08:30:00Z")
        );

        mockMvc.perform(post("/api/admin/users/12/roles")
                .with(httpBasic("admin@example.com", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "roleName": "CAREGIVER"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roles[0]").value("CAREGIVER"));
    }

    @Test
    void adminCanRemoveCaregiverRole() throws Exception {
        mockAdminUser("admin@example.com");
        when(roleManagementService.removeRole("admin@example.com", 12L, "CAREGIVER")).thenReturn(
            new AdminUserResponse(12L, "Role Test User", "role.test@example.com", "555-0199", true, List.of("USER"), "2026-07-01T08:30:00Z")
        );

        mockMvc.perform(delete("/api/admin/users/12/roles/CAREGIVER").with(httpBasic("admin@example.com", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    private void mockAdminUser(String username) {
        when(demoUserDetailsService.loadUserByUsername(username)).thenReturn(
            buildUser(username, "ROLE_ADMIN")
        );
    }

    private void mockUser(String username) {
        when(demoUserDetailsService.loadUserByUsername(username)).thenReturn(
            buildUser(username, "ROLE_USER")
        );
    }

    private static UserDetails buildUser(String username, String role) {
        return User.withUsername(username)
            .password("$2a$10$Ix3LesK9R3Y/mvnLAse3l.Sf3wxf0nnnRUsnq/dghoDIUfDf0IOUG")
            .authorities(role)
            .build();
    }
}
