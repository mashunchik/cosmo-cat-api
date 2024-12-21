package com.cosmo.cats.api.dto;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value
@Builder
public class ProductSalesReportDto {
    String productName;
    String categoryName;
    Long totalQuantitySold;
    BigDecimal totalRevenue;
    Double averageRating;
}
