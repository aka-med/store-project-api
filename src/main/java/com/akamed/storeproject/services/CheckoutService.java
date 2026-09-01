package com.akamed.storeproject.services;

import com.akamed.storeproject.dtos.CheckoutRequest;
import com.akamed.storeproject.dtos.CheckoutResponse;
import com.akamed.storeproject.entities.Order;
import com.akamed.storeproject.entities.PaymentStatus;
import com.akamed.storeproject.exceptions.CartEmptyException;
import com.akamed.storeproject.exceptions.CartNotFoundException;
import com.akamed.storeproject.exceptions.PaymentException;
import com.akamed.storeproject.repositories.CartRepository;
import com.akamed.storeproject.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final PaymentGateway paymentGateway;





    @Transactional
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

        try {
            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cart.getId());
            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException ex) {
            orderRepository.delete(order);
            throw ex;
        }
    }


    public void handWebhookEvent(WebhookRequest request) {

        paymentGateway
                .parseWebhookRequest(request)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });

    }
}
