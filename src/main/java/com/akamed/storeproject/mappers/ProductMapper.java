package com.akamed.storeproject.mappers;


import com.akamed.storeproject.dtos.ProductDto;
import com.akamed.storeproject.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    ProductDto toDto(Product product);
}
