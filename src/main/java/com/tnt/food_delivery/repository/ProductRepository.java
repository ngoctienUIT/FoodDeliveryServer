package com.tnt.food_delivery.repository;

import com.tnt.food_delivery.data.model.Product;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{ name: { $regex: ?0, $options: 'i' }, status: { $regex: ?1 } }")
    List<Product> findItem(String name, String status);
}
