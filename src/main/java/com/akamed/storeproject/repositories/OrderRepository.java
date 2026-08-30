package com.akamed.storeproject.repositories;

import com.akamed.storeproject.entities.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

}
