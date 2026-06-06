package carlos.jiang.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecentOrderSummaryResponse(
        Long orderId,
        String account,
        String customerName,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        int itemCount) {
}
