package com.akamed.storeproject.controllers;


import com.akamed.storeproject.dtos.CheckoutRequest;
import com.akamed.storeproject.dtos.CheckoutResponse;
import com.akamed.storeproject.dtos.ErrorDto;
import com.akamed.storeproject.exceptions.CartEmptyException;
import com.akamed.storeproject.exceptions.CartNotFoundException;
import com.akamed.storeproject.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/checkout")
@RestController
@AllArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;


    @PostMapping
    public CheckoutResponse checkout(
            @Valid @RequestBody CheckoutRequest request)
    {
        return checkoutService.checkout(request);
    }


    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }



}
