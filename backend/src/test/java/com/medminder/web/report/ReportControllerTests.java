package com.medminder.web.report;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.report.AdherenceReportService;
import com.medminder.web.dto.AdherenceSummaryResponse;
import com.medminder.web.dto.MonthlyAdherenceReportResponse;
import java.security.Principal;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessScopeService accessScopeService;

    @MockBean
    private AdherenceReportService adherenceReportService;

    @Test
    void userCanViewOwnAdherenceReport() throws Exception {
        when(accessScopeService.resolveSelfUserId("emily@example.com")).thenReturn(1L);
        when(adherenceReportService.getReport(1L, YearMonth.of(2026, 7))).thenReturn(
            new MonthlyAdherenceReportResponse(
                "2026-07",
                new AdherenceSummaryResponse(31, 20, 5, 3, 3, 64.52),
                List.of()
            )
        );

        mockMvc.perform(get("/api/reports/adherence")
                .queryParam("userId", "1")
                .queryParam("month", "2026-07")
                .principal((Principal) () -> "emily@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.month").value("2026-07"))
            .andExpect(jsonPath("$.summary.totalScheduledDoses").value(31))
            .andExpect(jsonPath("$.summary.totalTakenDoses").value(20));
    }

    @Test
    void userCannotViewAnotherUsersReport() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own records"))
            .when(accessScopeService)
            .requireSelfAccess("daniel@example.com", 1L);

        mockMvc.perform(get("/api/reports/adherence")
                .queryParam("userId", "1")
                .queryParam("month", "2026-07")
                .principal((Principal) () -> "daniel@example.com"))
            .andExpect(status().isForbidden());
    }
}
