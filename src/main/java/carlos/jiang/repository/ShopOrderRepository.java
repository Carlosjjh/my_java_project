package carlos.jiang.repository;

import carlos.jiang.model.ShopOrder;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {
    List<ShopOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<ShopOrder> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
            LocalDateTime from,
            LocalDateTime toExclusive);
}
