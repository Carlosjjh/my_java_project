package carlos.jiang.service;

import carlos.jiang.model.AppUser;
import carlos.jiang.repository.AppUserRepository;
import carlos.jiang.web.AuthenticationRequiredException;
import carlos.jiang.web.dto.LoginRequest;
import carlos.jiang.web.dto.ProfileRequest;
import carlos.jiang.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    public static final String SESSION_USER_ID = "USER_ID";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(RegisterRequest request) {
        String account = request.account().trim();
        if (userRepository.existsByAccount(account)) {
            throw new IllegalArgumentException("账号已存在");
        }
        AppUser user = new AppUser(
                request.username().trim(),
                account,
                passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AppUser login(LoginRequest request) {
        AppUser user = userRepository.findByAccount(request.account().trim())
                .orElseThrow(() -> new IllegalArgumentException("账号或密码错误"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public AppUser currentUser(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long id)) {
            throw new AuthenticationRequiredException("请先登录");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthenticationRequiredException("登录状态已失效，请重新登录"));
    }

    @Transactional(readOnly = true)
    public AppUser findCurrentUserOrNull(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long id)) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public AppUser updateProfile(AppUser user, ProfileRequest request) {
        user.updateProfile(
                request.recipientName().trim(),
                request.phone().trim(),
                request.address().trim());
        return userRepository.save(user);
    }

    public void remember(HttpSession session, AppUser user) {
        session.setAttribute(SESSION_USER_ID, user.getId());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
