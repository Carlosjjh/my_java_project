package carlos.jiang.web.dto;

import carlos.jiang.model.AppUser;

public record UserResponse(
        Long id,
        String username,
        String account,
        String recipientName,
        String phone,
        String address,
        boolean profileComplete) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAccount(),
                user.getRecipientName(),
                user.getPhone(),
                user.getAddress(),
                user.hasShippingProfile());
    }
}
