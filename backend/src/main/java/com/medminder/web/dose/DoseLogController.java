package com.medminder.web.dose;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.dose.DoseLogService;
import com.medminder.web.dto.DoseHistoryResponse;
import com.medminder.web.dto.DoseLogRequest;
import com.medminder.web.dto.DoseLogResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dose-logs")
public class DoseLogController {

    private final AccessScopeService accessScopeService;
    private final DoseLogService doseLogService;

    public DoseLogController(AccessScopeService accessScopeService, DoseLogService doseLogService) {
        this.accessScopeService = accessScopeService;
        this.doseLogService = doseLogService;
    }

    @PostMapping
    public DoseLogResponse logDose(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody DoseLogRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return doseLogService.logDose(authenticatedUserId, request);
    }

    @GetMapping("/history")
    public List<DoseHistoryResponse> getHistory(
        @RequestParam(required = false) Long userId,
        @RequestParam String startDate,
        @RequestParam String endDate,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return doseLogService.getHistory(authenticatedUserId, LocalDate.parse(startDate), LocalDate.parse(endDate));
    }
}
