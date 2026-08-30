package com.akamed.storeproject.controllers;


import com.akamed.storeproject.dtos.ErrorDto;
import com.akamed.storeproject.dtos.OrderDto;
import com.akamed.storeproject.exceptions.OrderNotFoundException;
import com.akamed.storeproject.mappers.OrderMapper;
import com.akamed.storeproject.repositories.OrderRepository;
import com.akamed.storeproject.services.AuthService;
import com.akamed.storeproject.services.OrderService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private OrderMapper orderMapper;


    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrder(
            @PathVariable("orderId") Long orderId
    ) {
        return orderService.getOrder(orderId);
    }



    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleOrderNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto(ex.getMessage()));
    }


}
