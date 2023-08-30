package com.tnt.food_delivery.service;

import com.tnt.food_delivery.data.model.OrderItem;
import com.tnt.food_delivery.data.request.OrderItemRequest;
import com.tnt.food_delivery.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    ProductRepository productRepository;

    @Autowired
    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<OrderItem> transformFrom(List<OrderItemRequest> listOrderItem) {
        return listOrderItem.stream()
                .map(e -> OrderItem.builder()
                        .product(productRepository.findById(e.getProductId()).get())
                        .quantity(e.getQuantity())
                        .selectedSize(e.getSelectedSize())
                        .build())
                .toList();
    }
}
