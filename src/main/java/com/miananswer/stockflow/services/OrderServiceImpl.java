package com.miananswer.stockflow.services;

import com.miananswer.stockflow.exceptions.InsufficientInventoryException;
import com.miananswer.stockflow.exceptions.ProductNotFoundException;
import com.miananswer.stockflow.models.dto.CreateOrderItemRequest;
import com.miananswer.stockflow.models.dto.CreateOrderRequest;
import com.miananswer.stockflow.models.dto.OrderItemResponse;
import com.miananswer.stockflow.models.dto.OrderResponse;
import com.miananswer.stockflow.models.entity.Order;
import com.miananswer.stockflow.models.entity.OrderItem;
import com.miananswer.stockflow.models.entity.OrderStatus;
import com.miananswer.stockflow.models.entity.Product;
import com.miananswer.stockflow.repositories.OrderRepository;
import com.miananswer.stockflow.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    ProductRepository productRepository;
    OrderRepository orderRepository;

    public OrderServiceImpl(ProductRepository productRepository, OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            if (product.getQuantity() < itemRequest.quantity()) {
                throw new InsufficientInventoryException(product.getId());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(itemRequest.quantity())
            );

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(itemRequest.quantity());

            product.setQuantity(
                    product.getQuantity() - itemRequest.quantity()
            );

            orderItems.add(orderItem);
            total = total.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);

        return createOrderResponse(savedOrder);
    }

    private OrderResponse createOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::createOrderItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                items
        );
    }

    private OrderItemResponse createOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getUnitPrice().multiply(
                        BigDecimal.valueOf(orderItem.getQuantity())
                )
        );
    }
}
