package com.akamed.storeproject.services;

import com.akamed.storeproject.entities.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class PaymentResult {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
