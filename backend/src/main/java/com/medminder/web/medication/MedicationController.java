package com.medminder.web.medication;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.medication.MedicationService;
import com.medminder.web.dto.CreateMedicationRequest;
import com.medminder.web.dto.MedicationResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final AccessScopeService accessScopeService;
    private final MedicationService medicationService;

    public MedicationController(AccessScopeService accessScopeService, MedicationService medicationService) {
        this.accessScopeService = accessScopeService;
        this.medicationService = medicationService;
    }

    @GetMapping
    public List<MedicationResponse> getMedications(@RequestParam(required = false) Long userId, Principal principal) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return medicationService.getMedications(authenticatedUserId);
    }

    @GetMapping("/{medicationId}")
    public MedicationResponse getMedication(
        @RequestParam(required = false) Long userId,
        @PathVariable Long medicationId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return medicationService.getMedication(authenticatedUserId, medicationId);
    }

    @PostMapping
    public MedicationResponse createMedication(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody CreateMedicationRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return medicationService.createMedication(authenticatedUserId, request);
    }

    @PutMapping("/{medicationId}")
    public MedicationResponse updateMedication(
        @RequestParam(required = false) Long userId,
        @PathVariable Long medicationId,
        @Valid @RequestBody CreateMedicationRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return medicationService.updateMedication(authenticatedUserId, medicationId, request);
    }

    @PatchMapping("/{medicationId}/deactivate")
    public MedicationResponse deactivateMedication(
        @RequestParam(required = false) Long userId,
        @PathVariable Long medicationId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return medicationService.deactivateMedication(authenticatedUserId, medicationId);
    }
}
