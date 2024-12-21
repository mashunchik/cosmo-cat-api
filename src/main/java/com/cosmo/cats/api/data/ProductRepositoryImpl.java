package com.cosmo.cats.api.data;

import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ConcurrentHashMap<String, Product> products = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        products.put("1", Product.builder()
                .id("1")
                .name("galaxy Jedi sword")
                .description("Mock description")
                .price(new BigDecimal("777"))
                .stockQuantity(10)
                .category(buildCategory("CAT-2"))
                .build());
        products.put("2", Product.builder()
                .id("2")
                .name("galaxy Lightsaber")
                .description("Mock description")
                .price(new BigDecimal("777"))
                .stockQuantity(10)
                .category(buildCategory("CAT-2"))
                .build());
    }

    private Category buildCategory(String categoryId) {
        return Category.builder()
                .id(categoryId)
                .name(getCategoryName(categoryId))
                .build();
    }

    private String getCategoryName(String categoryId) {
        return switch (categoryId) {
            case "CAT-1" -> "Space Vehicles";
            case "CAT-2" -> "Space Equipment";
            case "CAT-3" -> "Space Tools";
            default -> throw new IllegalArgumentException("Invalid category ID: " + categoryId);
        };
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Optional<Product> updateById(String id, Product updatedProduct) {
        return Optional.ofNullable(products.put(id, updatedProduct));
    }

    @Override
    public void deleteById(String id) {
        products.remove(id);
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public void resetRepository() {
        init();
    }

    public String generateNextId() {
        return "PROD-" + idCounter.incrementAndGet();
    }
}
