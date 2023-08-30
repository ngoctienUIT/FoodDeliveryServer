package com.tnt.food_delivery.data.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.tnt.food_delivery.data.model.Register.getCurrentTime;

@Data
@Builder(toBuilder = true)
@Document("rating")
@Getter
@Setter
@AllArgsConstructor
public class Rating {
    @Id
    private String id;

    @NonNull
    @DBRef
    private User user;

    private int rate;

    private String comment;

    @Builder.Default
    String createAt = getCurrentTime();

    @Builder.Default
    String updateAt = getCurrentTime();

    @Builder.Default
    Boolean isDelete = false;
}
