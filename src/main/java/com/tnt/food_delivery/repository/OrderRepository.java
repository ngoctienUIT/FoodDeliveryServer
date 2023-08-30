package com.tnt.food_delivery.repository;

import com.tnt.food_delivery.data.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    @Query("{ 'user._id' : ?0, 'status': { $regex: ?1 } }")
    List<Order> findOrder(String userIdentity, String status);

    @Query("{'status': { $regex: ?0 } }")
    List<Order> findOrder(String status);
}
