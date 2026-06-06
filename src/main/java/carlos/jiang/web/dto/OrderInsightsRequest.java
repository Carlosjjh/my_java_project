package carlos.jiang.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderInsightsRequest(
        LocalDate from,
        LocalDate to,
        @Min(0) BigDecimal minAmount,
        @Min(1) @Max(10) Integer topLimit) {
}
