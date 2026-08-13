package com.miananswer.stockflow.repositories;

import com.miananswer.stockflow.models.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
}
