package ar.com.lbr.precisionappbe.controller;

import ar.com.lbr.precisionappbe.model.LoginRequest;
import ar.com.lbr.precisionappbe.util.ApiResponse;
import ar.com.lbr.precisionappbe.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/login")
public class LoginController {

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody LoginRequest request) {
        return ResponseBuilder.ok(null, null, null);
    }
}
