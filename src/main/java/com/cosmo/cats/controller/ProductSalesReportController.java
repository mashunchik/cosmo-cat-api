package com.cosmo.cats.controller;

import com.cosmo.cats.api.dto.ProductSalesReportDto;
import com.cosmo.cats.service.ProductSalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports/product-sales")
@RequiredArgsConstructor
public class ProductSalesReportController {
    private final ProductSalesReportService reportService;

    @GetMapping("/top-selling")
    public ResponseEntity<Page<ProductSalesReportDto>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(reportService.getTopSellingProducts(startDate, endDate, pageable));
    }

    @GetMapping("/top-selling/category/{categoryId}")
    public ResponseEntity<List<ProductSalesReportDto>> getTopSellingProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "5") Long minOrders) {
        return ResponseEntity.ok(reportService.getTopSellingProductsByCategory(categoryId, startDate, endDate, minOrders));
    }
}
