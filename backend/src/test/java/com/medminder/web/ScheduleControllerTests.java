package com.medminder.web;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.schedule.ScheduleService;
import com.medminder.web.dto.ScheduleResponse;
import com.medminder.web.schedule.ScheduleController;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessScopeService accessScopeService;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    void userCanDeactivateOwnedSchedule() throws Exception {
        when(accessScopeService.resolveSelfUserId("emily@example.com")).thenReturn(1L);
        when(scheduleService.deactivateSchedule(1L, 101L)).thenReturn(
            new ScheduleResponse(101L, "08:00", "1", "DAILY", false)
        );

        mockMvc.perform(patch("/api/schedules/101/deactivate")
                .principal((Principal) () -> "emily@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false))
            .andExpect(jsonPath("$.scheduledTime").value("08:00"));
    }

    @Test
    void userCannotDeactivateAnotherUsersSchedule() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own records"))
            .when(accessScopeService)
            .requireSelfAccess("daniel@example.com", 1L);

        mockMvc.perform(patch("/api/schedules/101/deactivate")
                .queryParam("userId", "1")
                .principal((Principal) () -> "daniel@example.com"))
            .andExpect(status().isForbidden());
    }
}
