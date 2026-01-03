package com.example.orderproduct.order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderSummary, Long> {
}
