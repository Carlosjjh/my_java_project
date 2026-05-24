package carlos.jiang.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        String account,
        BigDecimal totalAmount,
        LocalDateTime createdAt) {
}
