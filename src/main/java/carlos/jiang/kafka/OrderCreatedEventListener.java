package carlos.jiang.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class OrderCreatedEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventListener.class);

    @KafkaListener(
            topics = "${app.kafka.order-created-topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(OrderCreatedEvent event) {
        log.info("Consumed order-created event: orderId={}, account={}, totalAmount={}",
                event.orderId(),
                event.account(),
                event.totalAmount());
    }
}
