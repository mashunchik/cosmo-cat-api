package com.cosmo.cats.service;

import com.cosmo.cats.api.dto.ProductSalesReportDto;
import com.cosmo.cats.persistence.projection.ProductSalesReport;
import com.cosmo.cats.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSalesReportService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductSalesReportDto> getTopSellingProducts(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        return productRepository.findTopSellingProducts(startDate, endDate, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<ProductSalesReportDto> getTopSellingProductsByCategory(
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long minOrders) {
        return productRepository.findTopSellingProductsByCategory(categoryId, startDate, endDate, minOrders)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ProductSalesReportDto mapToDto(ProductSalesReport report) {
        return ProductSalesReportDto.builder()
                .productName(report.getProductName())
                .categoryName(report.getCategoryName())
                .totalQuantitySold(report.getTotalQuantitySold())
                .totalRevenue(report.getTotalRevenue())
                .averageRating(report.getAverageRating())
                .build();
    }
}
