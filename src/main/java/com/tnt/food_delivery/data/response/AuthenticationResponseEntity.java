package com.tnt.food_delivery.data.response;

import com.tnt.food_delivery.data.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationResponseEntity {
    private String accessToken;
    private User user;
}
