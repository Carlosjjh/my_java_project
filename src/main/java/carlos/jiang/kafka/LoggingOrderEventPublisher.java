package carlos.jiang.kafka;

import carlos.jiang.model.ShopOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingOrderEventPublisher implements OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingOrderEventPublisher.class);

    @Override
    public void publishOrderCreated(ShopOrder order) {
        log.info("Kafka disabled. Order-created event retained as log only: orderId={}, account={}, totalAmount={}",
                order.getId(),
                order.getUser().getAccount(),
                order.getTotalAmount());
    }
}
