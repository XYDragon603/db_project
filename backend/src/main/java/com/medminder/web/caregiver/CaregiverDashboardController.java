package com.medminder.web.caregiver;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.caregiver.CaregiverDashboardService;
import com.medminder.web.dto.CaregiverPatientOverviewResponse;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caregiver")
public class CaregiverDashboardController {

    private final AccessScopeService accessScopeService;
    private final CaregiverDashboardService caregiverDashboardService;

    public CaregiverDashboardController(
        AccessScopeService accessScopeService,
        CaregiverDashboardService caregiverDashboardService
    ) {
        this.accessScopeService = accessScopeService;
        this.caregiverDashboardService = caregiverDashboardService;
    }

    @GetMapping("/dashboard")
    public List<CaregiverPatientOverviewResponse> getDashboard(@RequestParam Long caregiverId, Principal principal) {
        accessScopeService.requireCaregiverAccess(principal.getName(), caregiverId);
        return caregiverDashboardService.getOverview(caregiverId);
    }
}
