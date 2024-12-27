package com.cosmo.cats.api.service.impl;

import com.cosmo.cats.api.data.ProductRepository;
import com.cosmo.cats.api.data.ProductRepositoryImpl;
import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.service.ProductService;
import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepositoryImpl productRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product createProduct(Product product, String categoryId) {
        validateProductName(product.getName());
        Product newProduct = buildProduct(product, categoryId, productRepository.generateNextId());
        return productRepository.save(newProduct);
    }

    @Override
    public Product updateProduct(String id, Product updatedProduct, String categoryId) {
        Product existingProduct = getProduct(id);
        
        if (!existingProduct.getName().equals(updatedProduct.getName())) {
            validateProductName(updatedProduct.getName());
        }

        Product productWithUpdates = Product.builder()
                .id(id)
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .stockQuantity(updatedProduct.getStockQuantity())
                .category(buildCategory(categoryId))
                .build();

        return productRepository.updateById(id, productWithUpdates)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private void validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        productRepository.findAll().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .ifPresent(p -> {
                    throw new DuplicateProductNameException(name);
                });
    }

    private Category buildCategory(String categoryId) {
        return Category.builder()
                .id(categoryId)
                .name(getCategoryName(categoryId))
                .build();
    }

    private String getCategoryName(String categoryId) {
        return switch (categoryId) {
            case "CAT-1" -> "Space Equipment";
            case "CAT-2" -> "Space Gear";
            case "CAT-3" -> "Space Tools";
            default -> "Space Item";
        };
    }

    private Product buildProduct(Product product, String categoryId, String id) {
        return product.toBuilder()
                .id(id)
                .category(buildCategory(categoryId))
                .build();
    }
}