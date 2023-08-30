package com.tnt.food_delivery.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    private int rate;

    private String comment;
}
