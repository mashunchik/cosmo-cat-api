package com.cosmo.cats.persistence.projection;

import java.math.BigDecimal;

public interface ProductSalesReport {
    String getProductName();
    String getCategoryName();
    Long getTotalQuantitySold();
    BigDecimal getTotalRevenue();
    Double getAverageRating();
}
