package com.tnt.food_delivery.data.response;

import lombok.*;

@Data
@Builder()
@AllArgsConstructor
@NoArgsConstructor
public class CheckRegisterResponse {
    boolean status;
    String message;
}
