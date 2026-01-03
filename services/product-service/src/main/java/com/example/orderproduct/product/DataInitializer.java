package com.example.orderproduct.product;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(ProductRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new Product("Laptop", new BigDecimal("1299.99")),
                        new Product("Mouse", new BigDecimal("29.99")),
                        new Product("Keyboard", new BigDecimal("79.99"))
                ));
            }
        };
    }
}
