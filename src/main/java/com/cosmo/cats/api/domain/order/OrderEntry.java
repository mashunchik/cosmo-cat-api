package com.cosmo.cats.api.domain.order;

import com.cosmo.cats.api.domain.product.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class OrderEntry {
  @NotNull(message = "Product is required")
  Product product;
  
  @NotNull(message = "Quantity is required")
  @Min(value = 1, message = "Quantity must be at least 1")
  Integer quantity;
}
