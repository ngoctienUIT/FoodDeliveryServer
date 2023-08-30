package com.tnt.food_delivery.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    private String productId;
    private Integer quantity;
    private String selectedSize;
}
