package carlos.jiang.web.filter;

import carlos.jiang.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class LoginRequiredFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public LoginRequiredFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return !path.startsWith("/api/orders")
                && !("PUT".equalsIgnoreCase(method) && "/api/auth/profile".equals(path))
                && !("POST".equalsIgnoreCase(method) && "/api/auth/logout".equals(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Object userId = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute(AuthService.SESSION_USER_ID);

        if (userId instanceof Long) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("message", "请先登录"));
    }
}
