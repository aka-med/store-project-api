package com.akamed.storeproject.services;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.repository.cdi.Eager;

@Getter
@AllArgsConstructor
public class CheckoutSession {
    private String checkoutUrl;
}
