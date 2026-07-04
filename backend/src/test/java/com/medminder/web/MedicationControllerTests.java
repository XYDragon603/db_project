package com.medminder.web;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.medication.MedicationService;
import com.medminder.web.dto.MedicationResponse;
import com.medminder.web.medication.MedicationController;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(MedicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class MedicationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessScopeService accessScopeService;

    @MockBean
    private MedicationService medicationService;

    @Test
    void userCanDeactivateOwnedMedication() throws Exception {
        when(accessScopeService.resolveSelfUserId("emily@example.com")).thenReturn(1L);
        when(medicationService.deactivateMedication(1L, 101L)).thenReturn(
            new MedicationResponse(101L, "Metformin", "500mg", "Tablet", 20, 5, false, "2026-07-01", null, "Note")
        );

        mockMvc.perform(patch("/api/medications/101/deactivate")
                .principal((Principal) () -> "emily@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false))
            .andExpect(jsonPath("$.medicineName").value("Metformin"));
    }

    @Test
    void userCannotDeactivateAnotherUsersMedication() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own records"))
            .when(accessScopeService)
            .requireSelfAccess("daniel@example.com", 1L);

        mockMvc.perform(patch("/api/medications/101/deactivate")
                .queryParam("userId", "1")
                .principal((Principal) () -> "daniel@example.com"))
            .andExpect(status().isForbidden());
    }
}
