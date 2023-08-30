package com.tnt.food_delivery.controller;

import com.tnt.food_delivery.common.JwtUtils;
import com.tnt.food_delivery.data.model.Order;
import com.tnt.food_delivery.data.model.OrderItem;
import com.tnt.food_delivery.data.model.User;
import com.tnt.food_delivery.data.request.OrderRequest;
import com.tnt.food_delivery.repository.OrderRepository;
import com.tnt.food_delivery.repository.UserRepository;
import com.tnt.food_delivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tnt.food_delivery.data.model.Register.getCurrentTime;

@RestController
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private final OrderRepository orderRepository;

    @Autowired
    private final UserRepository userRepository;
    private final OrderService orderService;

    @GetMapping()
    public ResponseEntity<?> getOrderByUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam String userIdentity,
            @RequestParam String status
    ) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (role.equals("USER") && !userIdentity.isBlank()) {
                User user = userRepository.getUser(userIdentity);
                if (user.getId().equals(userID)) {
                    return ResponseEntity.ok(orderRepository.findOrder(user.getId(), status));
                }
            }
            if (userIdentity.isBlank() && role.equals("ADMIN")) {
                return ResponseEntity.ok(orderRepository.findOrder(status));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody OrderRequest orderRequest) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            if (role.equals("USER")) {
                Order order = Order.builder()
                        .user(userRepository.findById(userID).get())
                        .products(orderService.transformFrom(orderRequest.getProducts()))
                        .address(orderRequest.getAddress())
                        .wards(orderRequest.getWards())
                        .province(orderRequest.getProvince())
                        .district(orderRequest.getDistrict())
                        .build();
                return ResponseEntity.ok(orderRepository.save(order));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestBody OrderRequest orderRequest,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Order order = orderRepository.findById(id).get();
            if (role.equals("USER")
                    && userID.equals(order.getUser().getId())
                    && order.getStatus() == Order.StatusOrder.PENDING) {
                order.setPaymentMethod(orderRequest.getPaymentMethod());
                order.setProducts(orderService.transformFrom(orderRequest.getProducts()));
                order.setAddress(orderRequest.getAddress());
                order.setDistrict(orderRequest.getDistrict());
                order.setProvince(orderRequest.getProvince());
                order.setWards(orderRequest.getWards());
                order.setOrderNote(orderRequest.getOrderNote());
                order.setUpdateAt(getCurrentTime());

                return ResponseEntity.ok(orderRepository.save(order));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/placed")
    public ResponseEntity<?> placedOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Order order = orderRepository.findById(id).get();
            if (role.equals("USER")
                    && userID.equals(order.getUser().getId())
                    && order.getStatus() == Order.StatusOrder.PENDING) {
                HashMap<String, List<OrderItem>> hashMap = new HashMap<>();
                for (OrderItem item : order.getProducts()) {
                    String restaurantID = item.getProduct().getRestaurant().getId();
                    if (!hashMap.containsKey(restaurantID)) {
                        List<OrderItem> list = new ArrayList<>();
                        list.add(item);

                        hashMap.put(restaurantID, list);
                    } else {
                        hashMap.get(restaurantID).add(item);
                    }
                }
                for (var entry : hashMap.entrySet()) {
                    Order myOrder = Order.builder()
                            .products(entry.getValue())
                            .orderNote(order.getOrderNote())
                            .paymentMethod(order.getPaymentMethod())
                            .user(order.getUser())
                            .district(order.getDistrict())
                            .province(order.getProvince())
                            .wards(order.getWards())
                            .address(order.getAddress())
                            .createAt(order.getCreateAt())
                            .updateAt(order.getUpdateAt())
                            .status(Order.StatusOrder.PLACED)
                            .build();
                    orderRepository.save(myOrder);
                }
                orderRepository.deleteById(id);
                return ResponseEntity.ok("ok");
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Order order = orderRepository.findById(id).get();
            if (role.equals("RESTAURANT")
                    && userID.equals(order.getProducts().get(0).getProduct().getRestaurant().getId())
                    && order.getStatus() == Order.StatusOrder.PLACED) {
                order.setStatus(Order.StatusOrder.ACCEPT);
                return ResponseEntity.ok(orderRepository.save(order));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/delivery")
    public ResponseEntity<?> deliveryOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Order order = orderRepository.findById(id).get();
            if (role.equals("DELIVER")
                    && userID.equals(order.getUser().getId())
                    && order.getStatus() == Order.StatusOrder.ACCEPT) {
                order.setStatus(Order.StatusOrder.DELIVERY);
                return ResponseEntity.ok(orderRepository.save(order));
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable String id) {
        try {
            String content = JwtUtils.decodeJwtToken(token.split(" ")[1]).getSubject();
            String userID = content.split("~")[0];
            String role = content.split("~")[1];
            Order order = orderRepository.findById(id).get();
            if (role.equals("USER") && userID.equals(order.getUser().getId())) {
                orderRepository.deleteById(id);
                return ResponseEntity.ok("ok");
            }
            return ResponseEntity.badRequest().body("Bạn không có quyền thay đổi thông tin sản phẩm này");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
