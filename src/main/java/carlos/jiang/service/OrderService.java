package carlos.jiang.service;

import carlos.jiang.kafka.OrderEventPublisher;
import carlos.jiang.model.AppUser;
import carlos.jiang.model.OrderItem;
import carlos.jiang.model.Product;
import carlos.jiang.model.ShopOrder;
import carlos.jiang.repository.ProductRepository;
import carlos.jiang.repository.ShopOrderRepository;
import carlos.jiang.web.dto.CartItemRequest;
import carlos.jiang.web.dto.CreateOrderRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final ShopOrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(
            ProductRepository productRepository,
            ShopOrderRepository orderRepository,
            OrderEventPublisher orderEventPublisher) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public ShopOrder createOrder(CreateOrderRequest request, AppUser user) {
        if (!user.hasShippingProfile()) {
            throw new IllegalArgumentException("请先完善收货信息");
        }

        ShopOrder order = new ShopOrder(user, user.getRecipientName(), user.getPhone(), user.getAddress());

        for (CartItemRequest itemRequest : mergeSameProducts(request.items())) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + itemRequest.productId()));
            product.decreaseStock(itemRequest.quantity());
            order.addItem(new OrderItem(product, itemRequest.quantity()));
        }

        ShopOrder savedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(savedOrder);
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<ShopOrder> findOrdersByUser(AppUser user) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    private List<CartItemRequest> mergeSameProducts(List<CartItemRequest> items) {
        Map<Long, Integer> quantityByProductId = new LinkedHashMap<>();
        for (CartItemRequest item : items) {
            quantityByProductId.merge(item.productId(), item.quantity(), Integer::sum);
        }
        return quantityByProductId.entrySet().stream()
                .map(entry -> new CartItemRequest(entry.getKey(), entry.getValue()))
                .toList();
    }
}
