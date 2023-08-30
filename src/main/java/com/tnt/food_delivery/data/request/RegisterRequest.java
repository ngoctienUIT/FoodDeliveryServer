package com.tnt.food_delivery.data.request;

import com.tnt.food_delivery.data.model.Register;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
//    @NonNull
    private String userID;

//    @NonNull
    private Register.RegisterType type;
}
