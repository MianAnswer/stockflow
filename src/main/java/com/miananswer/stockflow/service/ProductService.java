package com.miananswer.stockflow.service;

import com.miananswer.stockflow.model.dto.CreateProductRequest;
import com.miananswer.stockflow.model.dto.ProductResponse;
import com.miananswer.stockflow.model.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);

    List<ProductResponse> getProducts();

    ProductResponse getProduct(Long id);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);
}
