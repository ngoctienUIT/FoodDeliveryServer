package com.tnt.food_delivery.controller;

import com.tnt.food_delivery.common.JwtUtils;
import com.tnt.food_delivery.data.model.Product;
import com.tnt.food_delivery.data.model.Rating;
import com.tnt.food_delivery.data.model.User;
import com.tnt.food_delivery.data.request.AuthenticationRequestEntity;
import com.tnt.food_delivery.data.request.CheckRegisterRequest;
import com.tnt.food_delivery.data.request.RatingRequest;
import com.tnt.food_delivery.data.request.UserRequest;
import com.tnt.food_delivery.data.response.AuthenticationResponseEntity;
import com.tnt.food_delivery.data.response.CheckRegisterResponse;
import com.tnt.food_delivery.repository.RatingRepository;
import com.tnt.food_delivery.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RatingRepository ratingRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestEntity authentication) {
        try {
            User user = userRepository.login(authentication.getUsername(), authentication.getPassword());
            AuthenticationResponseEntity response = new AuthenticationResponseEntity(JwtUtils.renderAccessToken(user), user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Tài khoản hoặc mật khẩu chưa chính xác");
        }
    }

    @PostMapping("/check-register")
    public ResponseEntity<?> checkRegister(@RequestBody CheckRegisterRequest register) {
        try {
            User user = userRepository.checkRegister(register.getUsername(), register.getEmail());
            if (user.getEmail().equals(register.getEmail())) {
                return ResponseEntity.ok().body(new CheckRegisterResponse(false, "Email đã tồn tại"));
            } else if (user.getUsername().equals(register.getUsername())) {
                return ResponseEntity.ok().body(new CheckRegisterResponse(false, "Username đã tồn tại"));
            }
            return ResponseEntity.ok(new CheckRegisterResponse(false, "Tài khoản đã tồn tại trong hệ thống"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.ok(new CheckRegisterResponse(true, "ok"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequest userRequest) {
        try {
            User myUser = User.builder()
                    .isMale(userRequest.getIsMale())
                    .name(userRequest.getName())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .email(userRequest.getEmail())
                    .birthOfDate(userRequest.getBirthOfDate())
                    .password(userRequest.getPassword())
                    .username(userRequest.getUsername())
                    .build();
            return ResponseEntity.ok(userRepository.save(myUser));
        } catch (DuplicateKeyException e) {
            int length = e.toString().split(":").length;
            String error = (e.toString().split(":")[length - 3]).split(" ")[1] + " is duplicated";
            return ResponseEntity.status(400).body(error);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e);
        }
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyUser() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByID(@PathVariable String id) {
        try {
            return ResponseEntity.ok(userRepository.findById(id).get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin người dùng");
        }
    }

//    @GetMapping("/restaurant")
//    public ResponseEntity<?> getAllRestaurant() {
//        try {
//            return ResponseEntity.ok(userRepository.getAllRestaurant());
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Không tìm thấy thông tin nhà hàng");
//        }
//    }

    @GetMapping("/restaurant")
    public ResponseEntity<?> searchRestaurant(@RequestParam String name) {
        try {
            return ResponseEntity.ok(userRepository.findRestaurantByName(name));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin nhà hàng");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id,
            @RequestBody User user) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (userID.equals(id) || role.equals("ADMIN")) {
                user.setId(id);
                return ResponseEntity.ok(userRepository.save(user));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin người dùng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/block")
    public ResponseEntity<?> blocUser(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
//            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (role.equals("ADMIN")) {
                User user = userRepository.findById(id).get();
                user.setStatus(User.UserStatus.BLOCKED);
                return ResponseEntity.ok(userRepository.save(user));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền khóa tài khoản người dùng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (userID.equals(id) || role.equals("ADMIN")) {
                User user = userRepository.findById(id).get();
                user.setStatus(User.UserStatus.DELETED);
                return ResponseEntity.ok(userRepository.save(user));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền xóa người dùng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/rating")
    public ResponseEntity<?> getRatingRestaurant(@PathVariable String id) {
        try {
            User user = userRepository.findById(id).get();
            if (user.getRatings() != null) {
                return ResponseEntity.ok(ratingRepository.findAllById(user.getRatings()));
            }
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<?> ratingRestaurant(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id,
            @RequestBody RatingRequest ratingRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            User user = userRepository.findById(id).get();
            Rating myRating = Rating.builder()
                    .rate(ratingRequest.getRate())
                    .comment(ratingRequest.getComment())
                    .user(userRepository.findById(userID).get())
                    .build();
            Rating rating = ratingRepository.save(myRating);
            if (role.equals("USER")
                    && user.getStatus() == User.UserStatus.ACTIVATED) {
                List<String> ratings;
                if (user.getRatings() != null) {
                    ratings = user.getRatings();
                } else {
                    ratings = new ArrayList<>();
                }
                ratings.add(rating.getId());
                user.setRatings(ratings);
                return ResponseEntity.ok(userRepository.save(user));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền rating nhà hàng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
