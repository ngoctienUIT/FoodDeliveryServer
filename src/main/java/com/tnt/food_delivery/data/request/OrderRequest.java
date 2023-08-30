package com.tnt.food_delivery.data.request;

import com.tnt.food_delivery.data.model.Order;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NonNull
    private List<OrderItemRequest> products;

    private Order.PaymentMethod paymentMethod = Order.PaymentMethod.CASH;

    private Order.StatusOrder status = Order.StatusOrder.PENDING;

    private String address;
    private String wards;
    private String district;
    private String province;
    private String orderNote;
}
