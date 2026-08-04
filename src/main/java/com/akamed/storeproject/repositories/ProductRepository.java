package com.akamed.storeproject.repositories;

import com.akamed.storeproject.dtos.ProductSummary;
import com.akamed.storeproject.dtos.ProductSummaryDTO;
import com.akamed.storeproject.entities.Category;
import com.akamed.storeproject.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {



    List<Product> findProductsByName(String name);
    List<Product> findProductsByNameOrderByPrice(String name);

    @Query("select p.id, p.name from Product p where p.category = :category")
    List<ProductSummary > findByCategory(@Param("category") Category category);



//    @Query(value = "select p from Product p where p.price between :min and :max order by p.name")
//    List<Product> findProducts(@Param("min") BigDecimal min,@Param("max") BigDecimal max);


//    @Query("select p from Product p join p.category where p.price between :min and :max order by p.name")
    @Procedure("findProductsByPrice")
    List<Product> findProducts(BigDecimal min, BigDecimal max);

    @Query("select count(*) from Product p where p.price between :min and :max")
    long countProducts(@Param("min") BigDecimal min, @Param("max") BigDecimal max);


    @Modifying
    @Query("update Product p set p.price = :newPrice where p.category.id = :categoryId")
    void updatePriceByCategory(@Param("newPrice") BigDecimal newPrice, @Param("categoryId") Byte categoryId);
}