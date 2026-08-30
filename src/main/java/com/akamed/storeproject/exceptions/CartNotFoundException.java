package com.akamed.storeproject.exceptions;

public class CartNotFoundException extends RuntimeException{
    public CartNotFoundException() {
        super("Cart not found");
    }
}
