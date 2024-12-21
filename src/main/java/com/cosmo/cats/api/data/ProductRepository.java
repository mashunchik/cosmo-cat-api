package com.cosmo.cats.api.data;

import com.cosmo.cats.api.domain.product.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    Optional<Product> findById(String id);

    List<Product> findAll();

    Optional<Product> updateById(String id, Product updatedProduct);

    void deleteById(String id);

    Product save(Product product);

    void resetRepository();

    String generateNextId();
}