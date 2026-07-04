package com.medminder.web.admin;

import com.medminder.service.admin.RoleManagementService;
import com.medminder.web.dto.AdminUserResponse;
import com.medminder.web.dto.AssignRoleRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class RoleManagementController {

    private final RoleManagementService roleManagementService;

    public RoleManagementController(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    @GetMapping("/roles/users")
    public List<AdminUserResponse> getRoleUsers() {
        return roleManagementService.getRoleUsers();
    }

    @PostMapping("/users/{userId}/roles")
    public AdminUserResponse assignRole(
        @PathVariable Long userId,
        @Valid @RequestBody AssignRoleRequest request,
        Principal principal
    ) {
        return roleManagementService.assignRole(principal.getName(), userId, request.roleName());
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public AdminUserResponse removeRole(
        @PathVariable Long userId,
        @PathVariable String roleName,
        Principal principal
    ) {
        return roleManagementService.removeRole(principal.getName(), userId, roleName);
    }
}
