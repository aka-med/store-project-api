package com.akamed.storeproject.mappers;

import com.akamed.storeproject.dtos.OrderDto;
import com.akamed.storeproject.dtos.OrderItemDto;
import com.akamed.storeproject.entities.Order;
import com.akamed.storeproject.entities.OrderItem;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);

    OrderItemDto toDto(OrderItem orderItem);
}
