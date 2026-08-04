package com.akamed.storeproject.repositories;

import com.akamed.storeproject.dtos.UserSummary;
import com.akamed.storeproject.entities.Product;
import com.akamed.storeproject.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    public List<User> findAllByWishListContains(Product product);

    @EntityGraph(attributePaths = {"tags", "addresses"})
    public Optional<User> findByEmail(String email);


    @EntityGraph(attributePaths = "addresses")
    @Query("select u from User u")
    List<User> findAllWithTags();

//    @EntityGraph(attributePaths = "profile")
    @Query("select u.id as id, u.email as email from User u where u.profile.loyaltyPoints > :value order by u.email")
    List<UserSummary> findLoyalUsers(@Param("value") int value);
}
