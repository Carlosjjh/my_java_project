package carlos.jiang.service;

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

    public OrderService(ProductRepository productRepository, ShopOrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public ShopOrder createOrder(CreateOrderRequest request) {
        ShopOrder order = new ShopOrder(request.customerName(), request.phone(), request.address());

        for (CartItemRequest itemRequest : mergeSameProducts(request.items())) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + itemRequest.productId()));
            product.decreaseStock(itemRequest.quantity());
            order.addItem(new OrderItem(product, itemRequest.quantity()));
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<ShopOrder> findAllOrders() {
        return orderRepository.findAll();
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
