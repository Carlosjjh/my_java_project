package carlos.jiang.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderInsightsResponse(
        LocalDate from,
        LocalDate to,
        long orderCount,
        BigDecimal totalSales,
        BigDecimal averageOrderValue,
        List<TopProductResponse> topProducts,
        List<RecentOrderSummaryResponse> recentOrders) {
}
