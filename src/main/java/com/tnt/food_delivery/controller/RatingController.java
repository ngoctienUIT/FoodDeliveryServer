package com.tnt.food_delivery.controller;

import com.tnt.food_delivery.common.JwtUtils;
import com.tnt.food_delivery.data.model.Rating;
import com.tnt.food_delivery.data.request.RatingRequest;
import com.tnt.food_delivery.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/rating")
public class RatingController {
    @Autowired
    RatingRepository ratingRepository;

    @PostMapping("/{id}")
    public ResponseEntity<?> updateRating(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id,
            @RequestBody RatingRequest ratingRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Rating rating = ratingRepository.findById(id).get();
            if (role.equals("USER")
                    && userID.equals(rating.getUser().getId())) {
                rating.setComment(ratingRequest.getComment());
                rating.setRate(ratingRequest.getRate());
                return ResponseEntity.ok(ratingRepository.save(rating));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền sửa rating sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRating(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Rating rating = ratingRepository.findById(id).get();
            if (role.equals("USER")
                    && userID.equals(rating.getUser().getId())) {
                rating.setIsDelete(true);
                return ResponseEntity.ok(ratingRepository.save(rating));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền xóa rating sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
