package com.medminder.web.report;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.report.AdherenceReportService;
import com.medminder.web.dto.MonthlyAdherenceReportResponse;
import java.security.Principal;
import java.time.YearMonth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final AccessScopeService accessScopeService;
    private final AdherenceReportService adherenceReportService;

    public ReportController(
        AccessScopeService accessScopeService,
        AdherenceReportService adherenceReportService
    ) {
        this.accessScopeService = accessScopeService;
        this.adherenceReportService = adherenceReportService;
    }

    @GetMapping("/adherence")
    public MonthlyAdherenceReportResponse getAdherenceReport(
        @RequestParam(required = false) Long userId,
        @RequestParam String month,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return adherenceReportService.getReport(authenticatedUserId, YearMonth.parse(month));
    }
}
