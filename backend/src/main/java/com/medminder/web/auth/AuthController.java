package com.medminder.web.auth;

import com.medminder.service.auth.AuthService;
import com.medminder.web.dto.AuthLoginRequest;
import com.medminder.web.dto.AuthLoginResponse;
import com.medminder.web.dto.AuthRegisterRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthLoginResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AuthLoginResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }
}
