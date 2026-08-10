package com.akamed.storeproject.mappers;


import com.akamed.storeproject.dtos.CartDto;
import com.akamed.storeproject.dtos.CartItemDto;
import com.akamed.storeproject.entities.Cart;
import com.akamed.storeproject.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
