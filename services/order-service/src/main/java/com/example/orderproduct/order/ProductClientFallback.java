package com.example.orderproduct.order;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductResponse getProductById(Long id) {
        ProductResponse fallback = new ProductResponse();
        fallback.setId(id);
        fallback.setName("Unavailable");
        fallback.setPrice(BigDecimal.ZERO);
        return fallback;
    }
}
