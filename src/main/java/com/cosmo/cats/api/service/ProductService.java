package com.cosmo.cats.api.service;

import com.cosmo.cats.api.domain.product.Product;
import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    
    Product getProduct(String id);
    
    Product createProduct(Product product, String categoryId);
    
    Product updateProduct(String id, Product product, String categoryId);
    
    void deleteProduct(String id);
}
