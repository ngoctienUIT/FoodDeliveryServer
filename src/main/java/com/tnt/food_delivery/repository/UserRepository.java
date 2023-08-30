package com.tnt.food_delivery.repository;


import com.tnt.food_delivery.data.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ name: { $regex: ?0, $options: 'i' }, userRole: RESTAURANT}")
    List<User> findRestaurantByName(String name);

    @Query("{ userRole: RESTAURANT }")
    List<User> getAllRestaurant();

    @Query("{ '$or':[ { 'username': ?0 , 'password': ?1 }, { 'email': ?0 , 'password': ?1 }, { 'phoneNumber': ?0 , 'password': ?1 } ] }")
    User login(String username, String password);

    @Query("{'$or': [ {'username': ?0} , {'email': ?1} ] }")
    User checkRegister(String username, String email);

    @Query("{'$or': [ {'username': ?0} , {'email': ?0}, {'phoneNumber': ?0} ] }")
    User getUser(String userIdentity);
}
