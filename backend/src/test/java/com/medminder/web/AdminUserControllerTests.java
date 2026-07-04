package com.medminder.web;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medminder.config.DemoUserDetailsService;
import com.medminder.service.admin.AdminUserService;
import com.medminder.web.admin.AdminUserController;
import com.medminder.web.dto.AdminUserResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminUserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private DemoUserDetailsService demoUserDetailsService;

    @Test
    void adminCanListUsers() throws Exception {
        mockAdminUser("admin@example.com");
        when(adminUserService.getUsers()).thenReturn(List.of(
            new AdminUserResponse(1L, "Emily Johnson", "emily@example.com", "555-0101", true, List.of("USER"), "2026-07-01T08:30:00Z")
        ));

        mockMvc.perform(get("/api/admin/users").with(httpBasic("admin@example.com", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("emily@example.com"))
            .andExpect(jsonPath("$[0].roles[0]").value("USER"));
    }

    @Test
    void nonAdminCannotListUsers() throws Exception {
        mockNonAdminUser("emily@example.com");

        mockMvc.perform(get("/api/admin/users").with(httpBasic("emily@example.com", "password")))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeactivateUser() throws Exception {
        mockAdminUser("admin@example.com");
        when(adminUserService.deactivateUser("admin@example.com", 11L)).thenReturn(
            new AdminUserResponse(11L, "Test User", "test.user@example.com", "555-0199", false, List.of("USER"), "2026-07-02T09:00:00Z")
        );

        mockMvc.perform(patch("/api/admin/users/11/deactivate").with(httpBasic("admin@example.com", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void adminCanReactivateUser() throws Exception {
        mockAdminUser("admin@example.com");
        when(adminUserService.reactivateUser("admin@example.com", 11L)).thenReturn(
            new AdminUserResponse(11L, "Test User", "test.user@example.com", "555-0199", true, List.of("USER"), "2026-07-02T09:00:00Z")
        );

        mockMvc.perform(patch("/api/admin/users/11/reactivate").with(httpBasic("admin@example.com", "password")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true));
    }

    private void mockAdminUser(String username) {
        when(demoUserDetailsService.loadUserByUsername(username)).thenReturn(
            buildUser(username, "ROLE_ADMIN")
        );
    }

    private void mockNonAdminUser(String username) {
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
