package carlos.jiang.web;

import carlos.jiang.service.OrderInsightsService;
import carlos.jiang.web.dto.OrderInsightsRequest;
import carlos.jiang.web.dto.OrderInsightsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Order Insights", description = "Operational order analysis APIs")
@RestController
@RequestMapping("/api/admin/order-insights")
public class AdminOrderInsightsController {
    private final OrderInsightsService orderInsightsService;

    public AdminOrderInsightsController(OrderInsightsService orderInsightsService) {
        this.orderInsightsService = orderInsightsService;
    }

    @Operation(summary = "Analyze orders by date range and product sales")
    @GetMapping
    public OrderInsightsResponse analyze(@Valid @ModelAttribute OrderInsightsRequest request) {
        return orderInsightsService.analyze(request);
    }
}
