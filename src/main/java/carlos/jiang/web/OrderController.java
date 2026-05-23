package carlos.jiang.web;

import carlos.jiang.model.AppUser;
import carlos.jiang.service.AuthService;
import carlos.jiang.service.OrderService;
import carlos.jiang.web.dto.CreateOrderRequest;
import carlos.jiang.web.dto.OrderResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final AuthService authService;

    public OrderController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @GetMapping
    public List<OrderResponse> findAll(HttpSession session) {
        AppUser user = authService.currentUser(session);
        return orderService.findOrdersByUser(user).stream().map(OrderResponse::from).toList();
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request, HttpSession session) {
        AppUser user = authService.currentUser(session);
        return OrderResponse.from(orderService.createOrder(request, user));
    }
}
