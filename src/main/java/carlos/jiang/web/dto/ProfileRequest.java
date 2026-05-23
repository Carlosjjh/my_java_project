package carlos.jiang.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileRequest(
        @NotBlank String recipientName,
        @NotBlank String phone,
        @NotBlank String address) {
}
