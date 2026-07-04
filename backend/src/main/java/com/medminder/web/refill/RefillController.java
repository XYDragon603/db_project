package com.medminder.web.refill;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.refill.RefillService;
import com.medminder.web.dto.CreateRefillRequest;
import com.medminder.web.dto.RefillAlertResponse;
import com.medminder.web.dto.RefillRecordResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/refills")
public class RefillController {

    private final AccessScopeService accessScopeService;
    private final RefillService refillService;

    public RefillController(AccessScopeService accessScopeService, RefillService refillService) {
        this.accessScopeService = accessScopeService;
        this.refillService = refillService;
    }

    @GetMapping("/alerts")
    public List<RefillAlertResponse> getAlerts(@RequestParam(required = false) Long userId, Principal principal) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return refillService.getAlerts(authenticatedUserId);
    }

    @PostMapping
    public RefillRecordResponse addRefill(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody CreateRefillRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return refillService.addRefill(authenticatedUserId, request);
    }
}
