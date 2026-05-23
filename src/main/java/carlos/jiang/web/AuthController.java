package carlos.jiang.web;

import carlos.jiang.model.AppUser;
import carlos.jiang.service.AuthService;
import carlos.jiang.web.dto.LoginRequest;
import carlos.jiang.web.dto.ProfileRequest;
import carlos.jiang.web.dto.RegisterRequest;
import carlos.jiang.web.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request, HttpSession session) {
        AppUser user = authService.register(request);
        authService.remember(session, user);
        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        AppUser user = authService.login(request);
        authService.remember(session, user);
        return UserResponse.from(user);
    }

    @GetMapping("/me")
    public UserResponse me(HttpSession session) {
        AppUser user = authService.findCurrentUserOrNull(session);
        return user == null ? null : UserResponse.from(user);
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(@Valid @RequestBody ProfileRequest request, HttpSession session) {
        AppUser user = authService.currentUser(session);
        return UserResponse.from(authService.updateProfile(user, request));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        authService.logout(session);
        return Map.of("message", "已退出登录");
    }
}
