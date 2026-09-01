package com.akamed.storeproject.controllers;


import com.akamed.storeproject.dtos.CheckoutRequest;
import com.akamed.storeproject.dtos.CheckoutResponse;
import com.akamed.storeproject.dtos.ErrorDto;
import com.akamed.storeproject.exceptions.CartEmptyException;
import com.akamed.storeproject.exceptions.CartNotFoundException;
import com.akamed.storeproject.exceptions.PaymentException;
import com.akamed.storeproject.repositories.OrderRepository;
import com.akamed.storeproject.services.CheckoutService;
import com.akamed.storeproject.services.WebhookRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequestMapping("/checkout")
@RestController
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;



    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request)
    {
        return checkoutService.checkout(request);
    }



    @PostMapping("/webhook")
    public void handleWebhook(
            @RequestHeader Map<String, String> headers,
            @RequestBody String payload
    ) {
            checkoutService.handWebhookEvent(new WebhookRequest(headers, payload));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorDto> handlePaymentException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }



}
