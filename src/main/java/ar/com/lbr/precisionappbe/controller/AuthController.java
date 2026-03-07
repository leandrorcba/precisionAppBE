package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.dto.AuthRequest;
import ar.com.lbr.precisionappbe.dto.response.AuthResponse;
import ar.com.lbr.precisionappbe.services.AuthenticationService;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        return ResponseBuilder.ok("Login ok", authenticationService.login(request), 0L);
    }
}
