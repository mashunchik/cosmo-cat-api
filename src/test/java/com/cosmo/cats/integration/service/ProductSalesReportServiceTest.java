package com.cosmo.cats.integration.service;

import com.cosmo.cats.api.dto.ProductSalesReportDto;
import com.cosmo.cats.integration.BaseIntegrationTest;
import com.cosmo.cats.persistence.entity.*;
import com.cosmo.cats.persistence.repository.CategoryRepository;
import com.cosmo.cats.persistence.repository.OrderRepository;
import com.cosmo.cats.persistence.repository.ProductRepository;
import com.cosmo.cats.service.ProductSalesReportService;
import com.cosmo.cats.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSalesReportServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProductSalesReportService reportService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private CategoryEntity category;
    private ProductEntity product;
    private OrderEntity order;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now().minusDays(7);
        endDate = LocalDateTime.now().plusDays(1);

        category = categoryRepository.save(TestDataBuilder.defaultCategory().build());
        product = productRepository.save(TestDataBuilder.defaultProduct(category).build());
        
        order = TestDataBuilder.defaultOrder().build();
        OrderItemEntity orderItem = TestDataBuilder.defaultOrderItem(order, product).build();
        order.addItem(orderItem);
        order = orderRepository.save(order);
    }

    @Test
    void shouldGetTopSellingProducts() {
        // when
        Page<ProductSalesReportDto> report = reportService.getTopSellingProducts(
                startDate,
                endDate,
                PageRequest.of(0, 10)
        );

        // then
        assertThat(report.getContent()).isNotEmpty();
        ProductSalesReportDto topProduct = report.getContent().get(0);
        assertThat(topProduct.getProductName()).isEqualTo(product.getName());
        assertThat(topProduct.getCategoryName()).isEqualTo(category.getName());
        assertThat(topProduct.getTotalQuantitySold()).isPositive();
    }

    @Test
    void shouldGetTopSellingProductsByCategory() {
        // when
        List<ProductSalesReportDto> reports = reportService.getTopSellingProductsByCategory(
                category.getId(),
                startDate,
                endDate,
                1L
        );

        // then
        assertThat(reports).isNotEmpty();
        ProductSalesReportDto report = reports.get(0);
        assertThat(report.getProductName()).isEqualTo(product.getName());
        assertThat(report.getCategoryName()).isEqualTo(category.getName());
        assertThat(report.getTotalQuantitySold()).isPositive();
    }
}
