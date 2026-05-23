package carlos.jiang.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String account,
        @NotBlank @Size(min = 6, message = "密码至少需要 6 位") String password) {
}
