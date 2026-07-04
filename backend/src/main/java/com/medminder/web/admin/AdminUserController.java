package com.medminder.web.admin;

import com.medminder.service.admin.AdminUserService;
import com.medminder.web.dto.AdminUserResponse;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<AdminUserResponse> getUsers() {
        return adminUserService.getUsers();
    }

    @PatchMapping("/{userId}/deactivate")
    public AdminUserResponse deactivateUser(@PathVariable Long userId, Principal principal) {
        return adminUserService.deactivateUser(principal.getName(), userId);
    }

    @PatchMapping("/{userId}/reactivate")
    public AdminUserResponse reactivateUser(@PathVariable Long userId, Principal principal) {
        return adminUserService.reactivateUser(principal.getName(), userId);
    }
}
