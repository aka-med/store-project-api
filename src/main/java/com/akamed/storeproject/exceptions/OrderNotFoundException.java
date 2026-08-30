package com.akamed.storeproject.exceptions;

import com.akamed.storeproject.dtos.OrderDto;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Order not found");
    }
}
