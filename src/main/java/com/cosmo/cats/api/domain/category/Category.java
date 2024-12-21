package com.cosmo.cats.api.domain.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Category {
  @NotNull(message = "Category ID is required")
  String id;
  
  @NotBlank(message = "Category name is required")
  @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
  String name;
}
