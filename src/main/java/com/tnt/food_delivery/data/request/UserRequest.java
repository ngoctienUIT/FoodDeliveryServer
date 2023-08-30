package com.tnt.food_delivery.data.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

@Setter
@Getter
@AllArgsConstructor
public class UserRequest {
    private String name;

//    @Builder.Default
    private Boolean isMale = true;
    private String birthOfDate;

//    @NonNull
//    @Indexed(unique = true)
    private String username;

//    @NonNull
    @Indexed(unique = true)
    private String email;

//    @NonNull
//    @Indexed(unique = true)
    private String phoneNumber;

//    @NonNull
    private String password;
}
