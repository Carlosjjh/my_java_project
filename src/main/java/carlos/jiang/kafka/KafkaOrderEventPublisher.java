package carlos.jiang.kafka;

import carlos.jiang.model.ShopOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaOrderEventPublisher implements OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String topic;

    public KafkaOrderEventPublisher(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            @Value("${app.kafka.order-created-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publishOrderCreated(ShopOrder order) {
        OrderCreatedEvent event = toEvent(order);
        try {
            kafkaTemplate.send(topic, order.getId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to publish order-created event for order {}", order.getId(), ex);
                        } else {
                            log.info("Published order-created event for order {} to topic {}", order.getId(), topic);
                        }
                    });
        } catch (RuntimeException ex) {
            log.warn("Kafka send rejected order-created event for order {}", order.getId(), ex);
        }
    }

    private OrderCreatedEvent toEvent(ShopOrder order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getAccount(),
                order.getTotalAmount(),
                order.getCreatedAt());
    }
}
