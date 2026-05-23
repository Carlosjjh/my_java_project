package carlos.jiang.web.dto;

import carlos.jiang.model.ShopOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String account,
        String customerName,
        String phone,
        String address,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponse> items) {
    public static OrderResponse from(ShopOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getAccount(),
                order.getCustomerName(),
                order.getPhone(),
                order.getAddress(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
