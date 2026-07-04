package com.medminder.web.caregiver;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.caregiver.CaregiverAccessService;
import com.medminder.web.dto.CaregiverAccessResponse;
import com.medminder.web.dto.GrantCaregiverAccessRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caregiver-access")
public class CaregiverAccessController {

    private final AccessScopeService accessScopeService;
    private final CaregiverAccessService caregiverAccessService;

    public CaregiverAccessController(
        AccessScopeService accessScopeService,
        CaregiverAccessService caregiverAccessService
    ) {
        this.accessScopeService = accessScopeService;
        this.caregiverAccessService = caregiverAccessService;
    }

    @GetMapping
    public List<CaregiverAccessResponse> getCaregiverAccess(
        @RequestParam(required = false) Long userId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return caregiverAccessService.getAccessRecords(authenticatedUserId);
    }

    @PostMapping
    public CaregiverAccessResponse grantCaregiverAccess(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody GrantCaregiverAccessRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return caregiverAccessService.grantAccess(authenticatedUserId, request.caregiverEmail());
    }

    @PatchMapping("/{accessId}/revoke")
    public CaregiverAccessResponse revokeCaregiverAccess(
        @RequestParam(required = false) Long userId,
        @PathVariable Long accessId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return caregiverAccessService.revokeAccess(authenticatedUserId, accessId);
    }
}
