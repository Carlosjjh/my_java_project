package carlos.jiang.service;

import carlos.jiang.model.OrderItem;
import carlos.jiang.model.ShopOrder;
import carlos.jiang.repository.ShopOrderRepository;
import carlos.jiang.web.dto.OrderInsightsRequest;
import carlos.jiang.web.dto.OrderInsightsResponse;
import carlos.jiang.web.dto.RecentOrderSummaryResponse;
import carlos.jiang.web.dto.TopProductResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderInsightsService {
    private static final int DEFAULT_TOP_LIMIT = 5;
    private static final int RECENT_ORDER_LIMIT = 5;

    private final ShopOrderRepository orderRepository;

    public OrderInsightsService(ShopOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public OrderInsightsResponse analyze(OrderInsightsRequest request) {
        LocalDate to = request.to() == null ? LocalDate.now() : request.to();
        LocalDate from = request.from() == null ? to.minusDays(6) : request.from();
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before or equal to to");
        }

        BigDecimal minAmount = request.minAmount() == null ? BigDecimal.ZERO : request.minAmount();
        int topLimit = request.topLimit() == null ? DEFAULT_TOP_LIMIT : request.topLimit();

        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();
        List<ShopOrder> orders = orderRepository
                .findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(fromTime, toExclusive)
                .stream()
                .filter(order -> order.getTotalAmount().compareTo(minAmount) >= 0)
                .toList();

        BigDecimal totalSales = orders.stream()
                .map(ShopOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageOrderValue = orders.isEmpty()
                ? BigDecimal.ZERO
                : totalSales.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);

        return new OrderInsightsResponse(
                from,
                to,
                orders.size(),
                totalSales,
                averageOrderValue,
                topProducts(orders, topLimit),
                recentOrders(orders));
    }

    private List<TopProductResponse> topProducts(List<ShopOrder> orders, int topLimit) {
        Map<Long, ProductAccumulator> products = new LinkedHashMap<>();
        for (ShopOrder order : orders) {
            for (OrderItem item : order.getItems()) {
                products.computeIfAbsent(
                        item.getProductId(),
                        productId -> new ProductAccumulator(item.getProductId(), item.getProductName()))
                        .add(item);
            }
        }

        return products.values().stream()
                .sorted(Comparator
                        .comparing(ProductAccumulator::sales).reversed()
                        .thenComparing(ProductAccumulator::quantity, Comparator.reverseOrder())
                        .thenComparing(ProductAccumulator::productName))
                .limit(topLimit)
                .map(ProductAccumulator::toResponse)
                .toList();
    }

    private List<RecentOrderSummaryResponse> recentOrders(List<ShopOrder> orders) {
        return orders.stream()
                .limit(RECENT_ORDER_LIMIT)
                .map(order -> new RecentOrderSummaryResponse(
                        order.getId(),
                        order.getUser().getAccount(),
                        order.getCustomerName(),
                        order.getTotalAmount(),
                        order.getCreatedAt(),
                        order.getItems().stream().mapToInt(OrderItem::getQuantity).sum()))
                .toList();
    }

    private static final class ProductAccumulator {
        private final Long productId;
        private final String productName;
        private long quantity;
        private BigDecimal sales = BigDecimal.ZERO;

        private ProductAccumulator(Long productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        private void add(OrderItem item) {
            quantity += item.getQuantity();
            sales = sales.add(item.getLineTotal());
        }

        private String productName() {
            return productName;
        }

        private Long quantity() {
            return quantity;
        }

        private BigDecimal sales() {
            return sales;
        }

        private TopProductResponse toResponse() {
            return new TopProductResponse(productId, productName, quantity, sales);
        }
    }
}
