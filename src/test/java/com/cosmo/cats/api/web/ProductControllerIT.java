package com.cosmo.cats.api.web;

import com.cosmo.cats.api.data.ProductRepository;
import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.service.ProductAdvisorService;
import com.cosmo.cats.api.dto.product.ProductCreationDto;
import com.cosmo.cats.api.dto.product.ProductUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIT {

    private static final String URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @SpyBean
    private ProductAdvisorService productAdvisorService;

    @BeforeEach
    void setUp() {
        productRepository.resetRepository();
    }

    @Test
    void shouldReturnProductById() throws Exception {
        mockMvc.perform(get(URL + "/1"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.category.id").value("CAT-2"),
                        jsonPath("$.name").value("galaxy Jedi sword"));
    }

    @Test
    void shouldThrowProductNotFoundException() throws Exception {
        mockMvc.perform(get(URL + "/999"))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.detail").value("Product with id 999 not found"),
                        jsonPath("$.type").value("product-not-found"),
                        jsonPath("$.title").value("Product not found"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(post(URL + "/category/{id}", "CAT-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildProductCreationDto("galaxy sword"))))
                .andExpect(status().isCreated());

        assertThat(productRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void shouldThrowDuplicateProductNameException() throws Exception {
        mockMvc.perform(post(URL + "/category/{id}", "CAT-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildProductCreationDto("galaxy Jedi sword"))))
                .andExpectAll(status().isBadRequest(),
                        jsonPath("$.detail").value("Product with name 'galaxy Jedi sword' already exists"),
                        jsonPath("$.type").value("this-name-exists"),
                        jsonPath("$.title").value("Duplicate name"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductUpdateDto update = ProductUpdateDto.builder()
                .name("galaxy Star Helmet")
                .description("Update description")
                .price(new BigDecimal("126"))
                .stockQuantity(12)
                .build();

        mockMvc.perform(put(URL + "/1/category/{categoryId}", "CAT-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById("1").orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("galaxy Star Helmet");
        assertThat(updatedProduct.getDescription()).isEqualTo("Update description");
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("126"));
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(12);
        assertThat(updatedProduct.getCategory().getId()).isEqualTo("CAT-2");
    }

    private static ProductCreationDto buildProductCreationDto(String name) {
        return ProductCreationDto.builder()
                .name(name)
                .description("Mock description")
                .price(new BigDecimal("777"))
                .stockQuantity(10)
                .build();
    }

    private static ProductUpdateDto buildProductUpdateDto(String name) {
        return ProductUpdateDto.builder()
                .name(name)
                .description("Update description")
                .price(new BigDecimal("126"))
                .stockQuantity(12)
                .build();
    }

    private static Stream<ProductCreationDto> buildUnValidProductCreationDto() {
        return Stream.of(
                buildProductCreationDto(""),
                buildProductCreationDto("Name without required words"),
                buildProductCreationDto(null),
                buildProductCreationDto("galaxy mock").toBuilder()
                        .price(new BigDecimal("0.002"))
                        .build(),
                buildProductCreationDto("galaxy").toBuilder()
                        .stockQuantity(-2)
                        .build()
        );
    }

    private static Stream<ProductUpdateDto> buildUnValidProductUpdateDto() {
        return Stream.of(
                buildProductUpdateDto(""),
                buildProductUpdateDto("Name without required words"),
                buildProductUpdateDto(null),
                buildProductUpdateDto("galaxy mock").toBuilder()
                        .price(new BigDecimal("0.002"))
                        .build(),
                buildProductUpdateDto("galaxy").toBuilder()
                        .stockQuantity(-2)
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("buildUnValidProductCreationDto")
    void shouldThrowMethodArgumentNotValidException(ProductCreationDto productCreationDto) throws Exception {
        mockMvc.perform(post(URL + "/category/{id}", "CAT-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCreationDto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("buildUnValidProductUpdateDto")
    void shouldThrowUnValidArgumentsExceptionUpdate(ProductUpdateDto productUpdateDto) throws Exception {
        mockMvc.perform(put(URL + "/1/category/{categoryId}", "CAT-2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productUpdateDto)))
                .andExpect(status().isBadRequest());
    }
}
