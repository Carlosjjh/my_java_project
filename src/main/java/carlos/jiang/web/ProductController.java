package carlos.jiang.web;

import carlos.jiang.model.Product;
import carlos.jiang.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Products", description = "Product browsing APIs")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Operation(summary = "List all products")
    @GetMapping
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Operation(summary = "Find one product by id")
    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + id));
    }
}
