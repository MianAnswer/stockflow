package com.miananswer.stockflow.services;

import com.miananswer.stockflow.models.dto.CreateProductRequest;
import com.miananswer.stockflow.models.dto.ProductResponse;
import com.miananswer.stockflow.models.dto.UpdateProductRequest;
import com.miananswer.stockflow.models.entity.Product;
import com.miananswer.stockflow.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with SKU already exists");
        }

        Product product = new Product();

        product.setName(request.name());
        product.setSku(request.sku());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        Product savedProduct = productRepository.save(product);

        return createProductResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::createProductResponse)
                .toList();
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product does not exist");
        }

        return createProductResponse(product.get());
    }

    @Override
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("Product does not exist");
        }

        Product product = optionalProduct.get();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        Product savedProduct = productRepository.save(product);

        return createProductResponse(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("Product does not exist");
        }

        productRepository.delete(optionalProduct.get());
    }

    private ProductResponse createProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
