package com.miananswer.stockflow.repository;

import com.miananswer.stockflow.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
