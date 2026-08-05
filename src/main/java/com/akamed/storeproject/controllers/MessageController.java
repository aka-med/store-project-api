package com.akamed.storeproject.controllers;


import com.akamed.storeproject.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @RequestMapping("/hello")
    public Message hello() {
        return new Message("hello mo!");
    }
}
