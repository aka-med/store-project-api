package com.akamed.storeproject.repositories;

import com.akamed.storeproject.dtos.UserSummary;
import com.akamed.storeproject.entities.Profile;
import com.akamed.storeproject.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProfileRepository extends CrudRepository<Profile, Long> {


    @EntityGraph(attributePaths = "user")
    @Query("select p.id as id, p.user.email from Profile p where p.loyaltyPoints > :value order by p.user.email")
    List<UserSummary> findByLoyaltyPointsGreaterThanOrderByUserEmail(@Param("value") int value);


//    @EntityGraph(attributePaths = "user")
//    @Query("select p.id as id, p.user.email as email from Profile p left join User u on u.id = p.id where p.loyaltyPoints > :value order by u.email")
//    List<UserSummary> findProfiles(@Param("value") int value);
}