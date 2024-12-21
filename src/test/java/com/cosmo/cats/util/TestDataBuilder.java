package com.cosmo.cats.util;

import com.cosmo.cats.persistence.entity.CategoryEntity;
import com.cosmo.cats.persistence.entity.ProductEntity;
import com.cosmo.cats.persistence.entity.OrderEntity;
import com.cosmo.cats.persistence.entity.OrderItemEntity;
import com.cosmo.cats.persistence.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestDataBuilder {

    public static CategoryEntity.CategoryEntityBuilder defaultCategory() {
        return CategoryEntity.builder()
                .name("Test Category")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static ProductEntity.ProductEntityBuilder defaultProduct(CategoryEntity category) {
        return ProductEntity.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(100)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static OrderEntity.OrderEntityBuilder defaultOrder() {
        return OrderEntity.builder()
                .cartId(UUID.randomUUID().toString())
                .totalAmount(BigDecimal.valueOf(99.99))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }

    public static OrderItemEntity.OrderItemEntityBuilder defaultOrderItem(OrderEntity order, ProductEntity product) {
        return OrderItemEntity.builder()
                .order(order)
                .product(product)
                .quantity(1)
                .pricePerUnit(product.getPrice())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());
    }
}
