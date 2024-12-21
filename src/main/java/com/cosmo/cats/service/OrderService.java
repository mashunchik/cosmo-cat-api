package com.cosmo.cats.service;

import com.cosmo.cats.api.domain.order.Order;
import com.cosmo.cats.api.domain.order.OrderEntry;
import com.cosmo.cats.persistence.entity.OrderEntity;
import com.cosmo.cats.persistence.entity.OrderItemEntity;
import com.cosmo.cats.persistence.entity.OrderStatus;
import com.cosmo.cats.persistence.entity.ProductEntity;
import com.cosmo.cats.persistence.repository.OrderRepository;
import com.cosmo.cats.persistence.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrder)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToOrder)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Order getOrderByCartId(String cartId) {
        return orderRepository.findByCartId(cartId)
                .map(this::mapToOrder)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with cart id: " + cartId));
    }

    @Transactional
    public Order createOrder(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setCartId(order.getCartId());
        entity.setTotalAmount(order.getTotalPrice());
        entity.setStatus(OrderStatus.PENDING);
        
        for (OrderEntry entry : order.getEntries()) {
            OrderItemEntity item = createOrderItem(entry);
            entity.addItem(item);
        }
        
        return mapToOrder(orderRepository.save(entity));
    }

    private OrderItemEntity createOrderItem(OrderEntry entry) {
        ProductEntity product = productRepository.findById(Long.valueOf(entry.getProductId()))
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + entry.getProductId()));
        
        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(product);
        item.setQuantity(entry.getQuantity());
        item.setPricePerUnit(product.getPrice());
        return item;
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        entity.setStatus(status);
        return mapToOrder(orderRepository.save(entity));
    }

    private Order mapToOrder(OrderEntity entity) {
        List<OrderEntry> entries = entity.getItems().stream()
                .map(item -> OrderEntry.builder()
                        .productId(String.valueOf(item.getProduct().getId()))
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return Order.builder()
                .id(String.valueOf(entity.getId()))
                .cartId(entity.getCartId())
                .totalPrice(entity.getTotalAmount())
                .entries(entries)
                .build();
    }
}
