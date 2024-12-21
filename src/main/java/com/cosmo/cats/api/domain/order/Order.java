package com.cosmo.cats.api.domain.order;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Order {
  @NotBlank(message = "Order ID is required")
  String id;
  
  @NotBlank(message = "Cart ID is required")
  String cartId;
  
  @NotNull(message = "Total price is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be greater than or equal to 0")
  BigDecimal totalPrice;
  
  @NotNull(message = "Order entries are required")
  @Size(min = 1, message = "Order must contain at least one entry")
  List<OrderEntry> entries;
}
