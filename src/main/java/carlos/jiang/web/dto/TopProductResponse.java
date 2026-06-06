package carlos.jiang.web.dto;

import java.math.BigDecimal;

public record TopProductResponse(
        Long productId,
        String productName,
        long quantity,
        BigDecimal sales) {
}
