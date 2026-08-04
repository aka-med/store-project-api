package com.akamed.storeproject.repositories;

import com.akamed.storeproject.entities.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}