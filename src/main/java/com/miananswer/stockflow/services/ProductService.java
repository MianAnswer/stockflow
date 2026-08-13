package com.miananswer.stockflow.services;

import com.miananswer.stockflow.models.dto.CreateProductRequest;
import com.miananswer.stockflow.models.dto.ProductResponse;
import com.miananswer.stockflow.models.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    List<ProductResponse> getProducts();

    ProductResponse getProduct(Long id);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);
}
