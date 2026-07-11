package com.medminder.web.schedule;

import com.medminder.service.auth.AccessScopeService;
import com.medminder.service.schedule.ScheduleService;
import com.medminder.web.dto.CreateScheduleRequest;
import com.medminder.web.dto.CreateSchedulesRequest;
import com.medminder.web.dto.ScheduleResponse;
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
@RequestMapping("/api")
public class ScheduleController {

    private final AccessScopeService accessScopeService;
    private final ScheduleService scheduleService;

    public ScheduleController(AccessScopeService accessScopeService, ScheduleService scheduleService) {
        this.accessScopeService = accessScopeService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/medications/{medicationId}/schedules")
    public List<ScheduleResponse> getSchedules(
        @RequestParam(required = false) Long userId,
        @PathVariable Long medicationId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return scheduleService.getSchedules(authenticatedUserId, medicationId);
    }

    @PostMapping("/schedules")
    public ScheduleResponse createSchedule(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody CreateScheduleRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return scheduleService.createSchedule(authenticatedUserId, request);
    }

    @PostMapping("/schedules/bulk")
    public List<ScheduleResponse> createSchedules(
        @RequestParam(required = false) Long userId,
        @Valid @RequestBody CreateSchedulesRequest request,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return scheduleService.createSchedules(authenticatedUserId, request.schedules());
    }

    @PatchMapping("/schedules/{scheduleId}/deactivate")
    public ScheduleResponse deactivateSchedule(
        @RequestParam(required = false) Long userId,
        @PathVariable Long scheduleId,
        Principal principal
    ) {
        var authenticatedUserId = accessScopeService.resolveSelfUserId(principal.getName());
        if (userId != null) {
            accessScopeService.requireSelfAccess(principal.getName(), userId);
        }
        return scheduleService.deactivateSchedule(authenticatedUserId, scheduleId);
    }
}
