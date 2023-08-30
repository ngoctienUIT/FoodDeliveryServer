package com.tnt.food_delivery.common;

import com.tnt.food_delivery.data.model.User;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;

public class JwtUtils {
    private static final long EXPIRE_DURATION_100_DAY = 24L * 3600 * 100000;

    public static String renderAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId() + "~" + user.getUserRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION_100_DAY))
                .compact();
    }

    public static DefaultClaims decodeJwtToken(String jwtToken) {
        return (DefaultClaims) Jwts.parserBuilder()
                .build()
                .parse(jwtToken)
                .getBody();
    }

}
