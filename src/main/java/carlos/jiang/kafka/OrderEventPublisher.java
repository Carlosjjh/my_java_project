package carlos.jiang.kafka;

import carlos.jiang.model.ShopOrder;

public interface OrderEventPublisher {
    void publishOrderCreated(ShopOrder order);
}
