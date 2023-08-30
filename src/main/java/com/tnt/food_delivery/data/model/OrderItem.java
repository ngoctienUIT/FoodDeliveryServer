package com.tnt.food_delivery.data.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Builder(toBuilder = true)
@Getter
@Setter
public class OrderItem {
    @DBRef
    private Product product;
    private Integer quantity;
    private String selectedSize;
}
