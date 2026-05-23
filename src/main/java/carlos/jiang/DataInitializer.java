package carlos.jiang;

import carlos.jiang.model.Product;
import carlos.jiang.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(List.of(
                new Product("经典白T恤", "柔软纯棉，适合日常通勤和周末出游。", new BigDecimal("79.00"),
                        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80", 120),
                new Product("城市双肩包", "轻量防泼水，电脑隔层可放 15 英寸设备。", new BigDecimal("199.00"),
                        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80", 45),
                new Product("陶瓷马克杯", "哑光釉面，350ml 容量，咖啡和茶都刚好。", new BigDecimal("49.00"),
                        "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?auto=format&fit=crop&w=900&q=80", 80),
                new Product("无线蓝牙耳机", "入门级音乐耳机，支持快充和触控操作。", new BigDecimal("269.00"),
                        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80", 32),
                new Product("桌面香薰", "清爽木质香，适合书桌、卧室和办公空间。", new BigDecimal("89.00"),
                        "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?auto=format&fit=crop&w=900&q=80", 54),
                new Product("机械键盘", "紧凑 84 键布局，适合学习、办公和轻度游戏。", new BigDecimal("329.00"),
                        "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80", 26)));
    }
}
