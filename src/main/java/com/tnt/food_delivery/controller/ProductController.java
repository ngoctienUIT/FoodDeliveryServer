package com.tnt.food_delivery.controller;

import com.tnt.food_delivery.common.JwtUtils;
import com.tnt.food_delivery.data.model.Product;
import com.tnt.food_delivery.data.model.Rating;
import com.tnt.food_delivery.data.request.ProductRequest;
import com.tnt.food_delivery.data.request.RatingRequest;
import com.tnt.food_delivery.repository.ProductRepository;

import com.tnt.food_delivery.repository.RatingRepository;
import com.tnt.food_delivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tnt.food_delivery.data.model.Register.getCurrentTime;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RatingRepository ratingRepository;

    @GetMapping("")
    public ResponseEntity<?> getProduct(@RequestParam String name, @RequestParam String status) {
        try {
            return ResponseEntity.ok(productRepository.findItem(name, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin sản phẩm");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductByID(@PathVariable String id) {
        try {
            return ResponseEntity.ok(productRepository.findById(id).get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin sản phẩm");
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createProduct(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody ProductRequest productRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (userID.equals(productRequest.getRestaurantID()) && role.equals("RESTAURANT")) {
                Product product = Product.builder()
                        .restaurant(userRepository.findById(productRequest.getRestaurantID()).get())
                        .name(productRequest.getName())
                        .description(productRequest.getDescription())
                        .image(productRequest.getImage())
                        .isSize(productRequest.getIsSize())
                        .s(productRequest.getS())
                        .l(productRequest.getL())
                        .m(productRequest.getM())
                        .price(productRequest.getPrice())
                        .build();
                return ResponseEntity.ok(productRepository.save(product));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Todo chưa tìm được cách tối ưu update
    @PostMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id,
            @RequestBody ProductRequest productRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Product product = productRepository.findById(id).get();
            if (role.equals("RESTAURANT")
                    && userID.equals(product.getRestaurant().getId())
                    && product.getStatus() != Product.ProductStatus.DELETE) {
                product.setUpdateAt(getCurrentTime());
                return ResponseEntity.ok(productRepository.save(product));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/launch")
    public ResponseEntity<?> acceptProduct(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Product product = productRepository.findById(id).get();
            if (role.equals("ADMIN")
                    || role.equals("RESTAURANT")
                    && userID.equals(product.getRestaurant().getId())
                    && product.getStatus() == Product.ProductStatus.CANCEL) {
                product.setUpdateAt(getCurrentTime());
                product.setStatus(Product.ProductStatus.LAUNCH);
                return ResponseEntity.ok(productRepository.save(product));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelProduct(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Product product = productRepository.findById(id).get();
            if (role.equals("ADMIN")
                    || role.equals("RESTAURANT")
                    && userID.equals(product.getRestaurant().getId())
                    && product.getStatus() == Product.ProductStatus.LAUNCH) {
                product.setUpdateAt(getCurrentTime());
                product.setStatus(Product.ProductStatus.CANCEL);
                return ResponseEntity.ok(productRepository.save(product));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Product product = productRepository.findById(id).get();
            if (role.equals("ADMIN")
                    || role.equals("RESTAURANT")
                    && userID.equals(product.getRestaurant().getId())) {
                product.setUpdateAt(getCurrentTime());
                product.setStatus(Product.ProductStatus.DELETE);
                return ResponseEntity.ok(productRepository.save(product));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/rating")
    public ResponseEntity<?> getRatingProduct(@PathVariable String id) {
        try {
            Product product = productRepository.findById(id).get();
            return ResponseEntity.ok(ratingRepository.findAllById(product.getRatings()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> ratingProduct(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id,
            @RequestBody RatingRequest ratingRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Product product = productRepository.findById(id).get();
            Rating myRating = Rating.builder()
                    .rate(ratingRequest.getRate())
                    .comment(ratingRequest.getComment())
                    .user(userRepository.findById(userID).get())
                    .build();
            Rating rating = ratingRepository.save(myRating);
            if (role.equals("USER")
                    && product.getStatus() == Product.ProductStatus.LAUNCH) {
                List<String> ratings = product.getRatings();
                ratings.add(rating.getId());
                product.setRatings(ratings);
                return ResponseEntity.ok(productRepository.save(product));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền rating sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
