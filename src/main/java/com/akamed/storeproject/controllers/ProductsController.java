package com.akamed.storeproject.controllers;


import com.akamed.storeproject.dtos.ProductDto;
import com.akamed.storeproject.entities.Product;
import com.akamed.storeproject.mappers.ProductMapper;
import com.akamed.storeproject.mappers.UserMapper;
import com.akamed.storeproject.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam(required = false, name = "categoryId") Byte categoryId
    ) {
        List<Product> products;
        if(categoryId != null)
            products = productRepository.findByCategoryId(categoryId);
        else
            products = productRepository.findAllWithCategory();
        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);

        if(product == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(productMapper.toDto(product));

    }
}
