package com.medminder.web.profile;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.profile.ProfileService;
import com.medminder.web.dto.ProfileResponse;
import com.medminder.web.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AccessScopeService accessScopeService;
    private final ProfileService profileService;

    public ProfileController(AccessScopeService accessScopeService, ProfileService profileService) {
        this.accessScopeService = accessScopeService;
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile(@RequestParam(required = false) Long userId, Principal principal) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return profileService.getProfile(authenticatedUserId);
    }

    @PutMapping
    public ProfileResponse updateProfile(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody UpdateProfileRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return profileService.updateProfile(authenticatedUserId, request);
    }
}
