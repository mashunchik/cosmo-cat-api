package com.cosmo.cats.integration.repository;

import com.cosmo.cats.integration.BaseIntegrationTest;
import com.cosmo.cats.persistence.entity.CategoryEntity;
import com.cosmo.cats.persistence.entity.ProductEntity;
import com.cosmo.cats.persistence.projection.ProductSalesReport;
import com.cosmo.cats.persistence.repository.CategoryRepository;
import com.cosmo.cats.persistence.repository.ProductRepository;
import com.cosmo.cats.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryEntity category;

    @BeforeEach
    void setUp() {
        category = categoryRepository.save(TestDataBuilder.defaultCategory().build());
    }

    @Test
    void shouldSaveProduct() {
        // given
        ProductEntity product = TestDataBuilder.defaultProduct(category).build();

        // when
        ProductEntity savedProduct = productRepository.save(product);

        // then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(product.getName());
        assertThat(savedProduct.getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    void shouldFindProductsByCategory() {
        // given
        ProductEntity product = productRepository.save(TestDataBuilder.defaultProduct(category).build());

        // when
        List<ProductEntity> products = productRepository.findByCategoryId(category.getId());

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getId()).isEqualTo(product.getId());
    }

    @Test
    void shouldFindTopSellingProducts() {
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<ProductSalesReport> report = productRepository.findTopSellingProducts(startDate, endDate, pageRequest);

        // then
        assertThat(report).isNotNull();
        assertThat(report.getContent()).isEmpty(); // No sales yet
    }

    @Test
    void shouldUpdateProduct() {
        // given
        ProductEntity product = productRepository.save(TestDataBuilder.defaultProduct(category).build());
        String newName = "Updated Product";
        product.setName(newName);

        // when
        ProductEntity updated = productRepository.save(product);

        // then
        assertThat(updated.getName()).isEqualTo(newName);
        assertThat(updated.getId()).isEqualTo(product.getId());
    }

    @Test
    void shouldDeleteProduct() {
        // given
        ProductEntity product = productRepository.save(TestDataBuilder.defaultProduct(category).build());

        // when
        productRepository.deleteById(product.getId());

        // then
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }
}
