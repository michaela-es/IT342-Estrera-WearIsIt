package edu.cit.estrera.wearisit.features.admin;

import edu.cit.estrera.wearisit.features.auth.AuthResponse;
import edu.cit.estrera.wearisit.features.auth.RegisterRequest;
import edu.cit.estrera.wearisit.infrastructure.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminWhitelistService;
    private final AdminService adminService;

    @GetMapping("pre/check-email")
    public ResponseEntity<ApiResponse<CheckEmailResponse>> checkEmail(@RequestParam("email") String email) {
        boolean allowed = adminWhitelistService.isEmailWhitelisted(email);
        CheckEmailResponse resp = new CheckEmailResponse(email, allowed);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @PostMapping("pre/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse response = adminService.registerAdminIfWhitelisted(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
