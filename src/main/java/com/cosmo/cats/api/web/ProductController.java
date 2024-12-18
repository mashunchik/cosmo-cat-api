package com.cosmo.cats.api.web;

import com.cosmo.cats.api.domain.product.Product;
import com.cosmo.cats.api.dto.product.ProductCreationDto;
import com.cosmo.cats.api.dto.product.ProductDto;
import com.cosmo.cats.api.dto.product.ProductUpdateDto;
import com.cosmo.cats.api.dto.product.advisor.ProductAdvisorResponseDto;
import com.cosmo.cats.api.service.ProductAdvisorService;
import com.cosmo.cats.api.service.ProductService;
import com.cosmo.cats.api.service.exception.InvalidProductIdException;
import com.cosmo.cats.api.web.mapper.ProductDtoMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;
    private final ProductAdvisorService productAdvisorService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(productDtoMapper.toProductDto(productService.getProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productDtoMapper.toProductDto(productService.getProduct(id)));
    }

    @GetMapping("/{id}/price-advisor")
    public ResponseEntity<ProductAdvisorResponseDto> getProductWithPriceAdvisor(@PathVariable String id) {
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(productAdvisorService.getProductPriceAdvice(product));
    }

    @PostMapping("/category/{categoryId}")
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody @Valid ProductCreationDto productDto,
            @PathVariable String categoryId) {
        Product product = productService.createProduct(
                productDtoMapper.toProduct(productDto),
                categoryId
        );
        return new ResponseEntity<>(
                productDtoMapper.toProductDto(product),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/category/{categoryId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable String id,
            @PathVariable String categoryId,
            @RequestBody @Valid ProductUpdateDto productDto) {
        Product product = productService.updateProduct(
                id,
                productDtoMapper.toProduct(productDto),
                categoryId
        );
        return ResponseEntity.ok(productDtoMapper.toProductDto(product));
    }
}