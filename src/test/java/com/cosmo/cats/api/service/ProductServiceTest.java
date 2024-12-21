package com.cosmo.cats.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.cosmo.cats.api.data.ProductRepository;
import com.cosmo.cats.api.domain.category.Category;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.service.exception.DuplicateProductNameException;
import com.cosmo.cats.api.service.exception.ProductNotFoundException;
import com.cosmo.cats.api.service.impl.ProductServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ProductServiceImpl.class})
public class ProductServiceTest {
    private final List<Product> MOCK_PRODUCTS = buildAllProductsMock();
    private final Product MOCK_PRODUCT = buildProductCreation();
    private final Product MOCK_PRODUCT_UPDATED = buildProductCreation().toBuilder()
            .id("PROD-1")
            .category(Category.builder().id("CAT2").name("Space item").build())
            .build();

    @MockBean
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Captor
    ArgumentCaptor<String> idCaptor;

    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    private static Stream<String> provideId() {
        return Stream.of("PROD-1", "PROD-2", "PROD-3");
    }

    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(MOCK_PRODUCTS);
        var result = productService.getProducts();

        assertEquals(MOCK_PRODUCTS.size(), result.size());
        assertEquals(MOCK_PRODUCTS, result);
    }

    @ParameterizedTest
    @MethodSource("provideId")
    void shouldReturnProductById(String id) {
        int index = Integer.parseInt(id.substring(5)) - 1;
        when(productRepository.findById(id)).thenReturn(Optional.of(MOCK_PRODUCTS.get(index)));

        var result = productService.getProduct(id);

        assertEquals(id, result.getId());
        assertEquals(MOCK_PRODUCTS.get(index), result);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenIdIsNonExistent() {
        when(productRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct("PROD-99"));
    }

    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepository.findAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.save(productArgumentCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(productRepository.generateNextId()).thenReturn("PROD-4");

        var result = productService.createProduct(MOCK_PRODUCT, "CAT2");

        assertNotNull(result);
        assertEquals("PROD-4", result.getId());
        assertEquals("CAT2", result.getCategory().getId());
    }

    @Test
    void shouldThrowDuplicateProductNameExceptionWhenCreatingWithExistingName() {
        when(productRepository.findAll()).thenReturn(MOCK_PRODUCTS);

        assertThrows(DuplicateProductNameException.class,
                () -> productService.createProduct(MOCK_PRODUCTS.get(0), "CAT2"));
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        when(productRepository.findAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.updateById(idCaptor.capture(),
                productArgumentCaptor.capture())).thenAnswer(inv ->
                Optional.of(inv.getArgument(1)));
        when(productRepository.findById("PROD-1")).thenReturn(Optional.of(MOCK_PRODUCTS.get(0)));

        var result = productService.updateProduct("PROD-1", MOCK_PRODUCT, "CAT2");

        assertEquals(MOCK_PRODUCT_UPDATED.getId(), result.getId());
        assertEquals(MOCK_PRODUCT_UPDATED.getCategory(), result.getCategory());
    }

    @Test
    void shouldUpdateProductWithNewIdWhenProductIdIsNonExistent() {
        when(productRepository.findAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.save(productArgumentCaptor.capture())).thenAnswer(
                inv -> inv.getArgument(0));

        var result = productService.updateProduct("PROD-7", MOCK_PRODUCT, "CAT2");

        assertEquals("PROD-7", result.getId());
        assertEquals("CAT2", result.getCategory().getId());
        assertEquals(MOCK_PRODUCT.getName(), result.getName());
    }

    @Test
    void shouldThrowDuplicateProductNameExceptionWhenUpdatingWithExistingName() {
        when(productRepository.findAll()).thenReturn(MOCK_PRODUCTS);
        when(productRepository.findById(anyString())).thenReturn(Optional.of(MOCK_PRODUCTS.get(0)));

        assertThrows(DuplicateProductNameException.class,
                () -> productService.updateProduct("PROD-1", MOCK_PRODUCTS.get(1), "CAT2"));
    }

    private Product buildProductCreation() {
        return Product.builder()
                .name("NonExistent name")
                .price(BigDecimal.valueOf(1223))
                .stockQuantity(10)
                .description("Description")
                .build();
    }

    private List<Product> buildAllProductsMock() {
        return List.of(
                Product.builder()
                        .id("PROD-1")
                        .name("Star Mock1")
                        .description("Description mock1")
                        .price(BigDecimal.valueOf(199.99))
                        .stockQuantity(10)
                        .category(Category.builder().id("CAT1").name("Mock1 category").build())
                        .build(),
                Product.builder()
                        .id("PROD-2")
                        .name("Star Mock2")
                        .description("Description mock2")
                        .price(BigDecimal.valueOf(299.99))
                        .stockQuantity(20)
                        .category(Category.builder().id("CAT2").name("Mock2 category").build())
                        .build(),
                Product.builder()
                        .id("PROD-3")
                        .name("Star Mock3")
                        .description("Description mock3")
                        .price(BigDecimal.valueOf(399.99))
                        .stockQuantity(30)
                        .category(Category.builder().id("CAT3").name("Mock3 category").build())
                        .build()
        );
    }
}
