package com.medminder.web.dashboard;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.dashboard.DashboardService;
import com.medminder.web.dto.UserDashboardResponse;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class UserDashboardController {

    private final AccessScopeService accessScopeService;
    private final DashboardService dashboardService;

    public UserDashboardController(AccessScopeService accessScopeService, DashboardService dashboardService) {
        this.accessScopeService = accessScopeService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/today")
    public UserDashboardResponse getDashboard(
        @RequestParam(required = false) Long userId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return dashboardService.getDashboard(authenticatedUserId);
    }
}
