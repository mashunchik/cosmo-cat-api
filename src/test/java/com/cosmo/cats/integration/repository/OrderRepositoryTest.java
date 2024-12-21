package com.cosmo.cats.integration.repository;

import com.cosmo.cats.integration.BaseIntegrationTest;
import com.cosmo.cats.persistence.entity.*;
import com.cosmo.cats.persistence.repository.CategoryRepository;
import com.cosmo.cats.persistence.repository.OrderRepository;
import com.cosmo.cats.persistence.repository.ProductRepository;
import com.cosmo.cats.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryEntity category;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(TestDataBuilder.defaultCategory().build());
        product = productRepository.save(TestDataBuilder.defaultProduct(category).build());
    }

    @Test
    void shouldSaveOrder() {
        // given
        OrderEntity order = TestDataBuilder.defaultOrder().build();
        OrderItemEntity orderItem = TestDataBuilder.defaultOrderItem(order, product).build();
        order.addItem(orderItem);

        // when
        OrderEntity savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCartId()).isNotNull();
        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldFindOrderByCartId() {
        // given
        OrderEntity order = TestDataBuilder.defaultOrder().build();
        OrderItemEntity orderItem = TestDataBuilder.defaultOrderItem(order, product).build();
        order.addItem(orderItem);
        orderRepository.save(order);

        // when
        Optional<OrderEntity> found = orderRepository.findByCartId(order.getCartId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCartId()).isEqualTo(order.getCartId());
        assertThat(found.get().getItems()).hasSize(1);
    }

    @Test
    void shouldUpdateOrderStatus() {
        // given
        OrderEntity order = TestDataBuilder.defaultOrder().build();
        OrderItemEntity orderItem = TestDataBuilder.defaultOrderItem(order, product).build();
        order.addItem(orderItem);
        order = orderRepository.save(order);

        // when
        order.setStatus(OrderStatus.CONFIRMED);
        OrderEntity updated = orderRepository.save(order);

        // then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void shouldDeleteOrder() {
        // given
        OrderEntity order = TestDataBuilder.defaultOrder().build();
        OrderItemEntity orderItem = TestDataBuilder.defaultOrderItem(order, product).build();
        order.addItem(orderItem);
        order = orderRepository.save(order);

        // when
        orderRepository.deleteById(order.getId());

        // then
        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }
}
