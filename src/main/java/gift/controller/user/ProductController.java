package gift.controller.user;

import static gift.util.RoleUtil.extractRole;

import gift.dto.product.ProductRequest;
import gift.dto.product.ProductResponse;
import gift.entity.product.Product;
import gift.exception.custom.ProductNotFoundException;
import gift.service.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService svc) {
        this.productService = svc;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll(HttpServletRequest request) {
        List<ProductResponse> list = productService.getAllProducts(extractRole(request)).stream()
                .map(Product::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            HttpServletRequest request,
            @PathVariable Long id) {
        Product p = productService.getProductById(id, extractRole(request))
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(p.toResponse());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            HttpServletRequest request,
            @Valid @RequestBody ProductRequest dto) {
        Product saved = productService.createProduct(
                dto.name(), dto.price(), dto.imageUrl(), extractRole(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.toResponse());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest dto) {
        Product updated = productService.updateProduct(
                id, dto.name(), dto.price(), dto.imageUrl(), extractRole(request));
        return ResponseEntity.ok(updated.toResponse());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpServletRequest request,
            @PathVariable Long id) {
        productService.deleteProduct(id, extractRole(request));
        return ResponseEntity.noContent().build();
    }
}
