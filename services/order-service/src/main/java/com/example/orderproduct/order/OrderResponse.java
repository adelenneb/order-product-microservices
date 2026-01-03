package com.example.orderproduct.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private Instant createdAt;
    private OrderStatus status;
    private BigDecimal total;
    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId, Instant createdAt, OrderStatus status, BigDecimal total, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.status = status;
        this.total = total;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
