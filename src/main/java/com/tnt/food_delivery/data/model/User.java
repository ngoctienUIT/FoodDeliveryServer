package com.tnt.food_delivery.data.model;

import com.mongodb.lang.NonNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder(toBuilder = true)
@Document("user")
@Getter
@Setter
@AllArgsConstructor
public class User {
    @Id
    private String id;

    @NonNull
    private String name;

    @Builder.Default
    private Boolean isMale = true;

    private String birthOfDate;

    private String avatar;

    @NonNull
    @Indexed(unique = true)
    private String username;

    @NonNull
    @Indexed(unique = true)
    private String email;

    @NonNull
    @Indexed(unique = true)
    private String phoneNumber;

    @NonNull
    private String password;

    private String description;
    private String address;
    private String wards;
    private String district;
    private String province;

    private List<String> ratings; // chỉ sử dụng cho role RESTAURANT

    @Builder.Default
    private UserRole userRole = UserRole.USER;

    @Builder.Default
    private UserStatus status = UserStatus.ACTIVATED;

    public enum UserRole {
        NO_ROLE,
        USER,
        DELIVER,
        RESTAURANT,
        ADMIN
    }

    public enum UserStatus {
        NOT_VERIFIED,
        ACTIVATED,
        BLOCKED,
        DELETED,
    }
}
