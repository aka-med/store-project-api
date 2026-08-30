package com.akamed.storeproject.services;

import com.akamed.storeproject.dtos.CheckoutRequest;
import com.akamed.storeproject.dtos.CheckoutResponse;
import com.akamed.storeproject.entities.Order;
import com.akamed.storeproject.exceptions.CartEmptyException;
import com.akamed.storeproject.exceptions.CartNotFoundException;
import com.akamed.storeproject.repositories.CartRepository;
import com.akamed.storeproject.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AuthService authService;

    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart == null) {
            throw new CartNotFoundException();

        }
        if(cart.isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());
        orderRepository.save(order);
        cartService.clearCart(cart.getId());
        return new CheckoutResponse(order.getId());
    }
}
