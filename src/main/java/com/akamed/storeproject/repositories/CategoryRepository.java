package com.akamed.storeproject.repositories;

import com.akamed.storeproject.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}