package com.tnt.food_delivery.data.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static com.tnt.food_delivery.data.model.Register.getCurrentTime;

@Data
@Builder(toBuilder = true)
@Document("order")
@Getter
@Setter
@AllArgsConstructor
public class Order {
    @Id
    private String id;

    @NonNull
    @DBRef
    private User user;

    @Builder.Default
    private String createAt = getCurrentTime();

    @Builder.Default
    private String updateAt = getCurrentTime();

    @Builder.Default
    private List<OrderItem> products = new ArrayList<>();

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Builder.Default
    private StatusOrder status = StatusOrder.PENDING;

    private String address;
    private String wards;
    private String district;
    private String province;
    private String orderNote;

    public enum StatusOrder {
        PENDING, PLACED, ACCEPT, DELIVERY, SUCCESS, CANCEL
    }

    public enum PaymentMethod {
        CASH, PAYPAL,
    }
}
