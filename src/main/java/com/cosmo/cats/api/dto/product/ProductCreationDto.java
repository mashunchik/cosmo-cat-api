package com.cosmo.cats.api.dto.product;


import com.cosmo.cats.api.dto.validation.CosmicWordCheck;
import com.cosmo.cats.api.dto.validation.ExtendedValidation;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;


@Value
@Builder(toBuilder = true)
@GroupSequence({ProductCreationDto.class, ExtendedValidation.class})
public class ProductCreationDto {

  @NotNull(message = "Product name is required")
  @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
  @CosmicWordCheck(groups = ExtendedValidation.class)
  String name;

  @NotNull(message = "Description is required")
  @NotBlank(message = "Description cannot be blank")
  @Size(max = 255, message = "Description must not exceed 255 characters")
  String description;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.01", message = "Price must be greater than zero")
  @Digits(integer = 7, fraction = 2, message = "Price must have up to 7 digits before the decimal and up to 2 after")
  BigDecimal price;

  @NotNull(message = "Stock quantity is required")
  @Positive(message = "Stock quantity must be greater than zero")
  Integer stockQuantity;
}
