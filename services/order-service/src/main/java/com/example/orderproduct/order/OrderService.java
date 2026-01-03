package com.example.orderproduct.order;

import feign.FeignException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

    private final ProductClient productClient;
    private final OrderRepository orderRepository;

    public OrderService(ProductClient productClient, OrderRepository orderRepository) {
        this.productClient = productClient;
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(OrderRequest request) {
        ProductResponse product = fetchProduct(request.getProductId());
        if (product.getId() == null || product.getPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Product data unavailable");
        }

        OrderSummary order = new OrderSummary(OrderStatus.CREATED);
        OrderItem item = new OrderItem(
                product.getId(),
                request.getQuantity(),
                product.getPrice(),
                Optional.ofNullable(product.getName()).orElse("Unknown product"));
        order.addItem(item);

        OrderSummary saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    public OrderResponse getOrder(Long id) {
        OrderSummary order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
        return mapToResponse(order);
    }

    private ProductResponse fetchProduct(Long productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + productId);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot reach product-service", e);
        }
    }

    private OrderResponse mapToResponse(OrderSummary order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getProductId(),
                        i.getProductName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getLineTotal()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotal(),
                itemResponses
        );
    }
}
