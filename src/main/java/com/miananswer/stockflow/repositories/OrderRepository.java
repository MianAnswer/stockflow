package com.miananswer.stockflow.repositories;

import com.miananswer.stockflow.models.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
