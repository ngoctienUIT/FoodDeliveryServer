package com.tnt.food_delivery.controller;

import com.tnt.food_delivery.common.JwtUtils;
import com.tnt.food_delivery.data.model.Register;
import com.tnt.food_delivery.data.model.User;
import com.tnt.food_delivery.data.request.RegisterRequest;
import com.tnt.food_delivery.repository.RegisterRepository;
import com.tnt.food_delivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/register")
public class RegisterController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RegisterRepository registerRepository;

    @GetMapping("")
    public ResponseEntity<?> getRegister(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam String type,
            @RequestParam String status) {
        if (token != null) {
            try {
                String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
//                String userID = content.split("~")[0];
                String role = content.split("~")[1];
                if (role.equals("ADMIN")) {
                    return ResponseEntity.ok(registerRepository.findItem(type, status));
                }
                return ResponseEntity.badRequest().body("Bạn không có quyền truy cập chức năng này");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Bạn không có quyền truy cập chức năng này");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> register(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody RegisterRequest registerRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (userID.equals(registerRequest.getUserID()) && role.equals("USER") || role.equals("ADMIN")) {
                Register register = Register.builder()
                        .user(userRepository.findById(registerRequest.getUserID()).get())
                        .type(registerRequest.getType())
                        .build();
                return ResponseEntity.ok(registerRepository.save(register));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền truy cập chức năng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}") //id register
    public ResponseEntity<?> acceptRegister(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
//            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Register register = registerRepository.findById(id).get();
            if (role.equals("ADMIN")) {
                User user = register.getUser();
                if (register.getType() == Register.RegisterType.RESTAURANT) {
                    user.setUserRole(User.UserRole.RESTAURANT);
                } else {
                    user.setUserRole(User.UserRole.DELIVER);
                }
                register.setStatus(Register.RegisterStatus.ACCEPT);
                register.setTimeUpdate(Register.getCurrentTime());
                registerRepository.save(register);
                return ResponseEntity.ok(userRepository.save(user));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền truy cập chức năng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}") //id register
    public ResponseEntity<?> cancelRegister(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Register register = registerRepository.findById(id).get();
            if (userID.equals(register.getUser().getId()) && role.equals("USER") || role.equals("ADMIN")) {
                register.setStatus(Register.RegisterStatus.CANCEL);
                register.setTimeUpdate(Register.getCurrentTime());
                return ResponseEntity.ok(registerRepository.save(register));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền truy cập chức năng này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
