package com.cosmo.cats.service;

import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.persistence.entity.CategoryEntity;
import com.cosmo.cats.persistence.entity.ProductEntity;
import com.cosmo.cats.persistence.repository.CategoryRepository;
import com.cosmo.cats.persistence.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProduct)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToProduct)
                .collect(Collectors.toList());
    }

    @Transactional
    public Product createProduct(Product product) {
        ProductEntity entity = new ProductEntity();
        updateEntityFromProduct(entity, product);
        return mapToProduct(productRepository.save(entity));
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        updateEntityFromProduct(entity, product);
        return mapToProduct(productRepository.save(entity));
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private void updateEntityFromProduct(ProductEntity entity, Product product) {
        CategoryEntity category = categoryRepository.findById(Long.valueOf(product.getCategory().getId()))
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + product.getCategory().getId()));
        
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStockQuantity(product.getStockQuantity());
        entity.setCategory(category);
    }

    private Product mapToProduct(ProductEntity entity) {
        return Product.builder()
                .id(String.valueOf(entity.getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .category(categoryService.getCategoryById(entity.getCategory().getId()))
                .build();
    }
}
