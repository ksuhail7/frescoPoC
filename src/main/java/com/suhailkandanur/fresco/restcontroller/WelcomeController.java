package com.suhailkandanur.fresco.restcontroller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by suhail on 2016-12-01.
 */
@RestController
public class WelcomeController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/welcome")
    public String welcome() {
        String token = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend("fresco", "", token);
        return "Welcome to Fresco [" + token + "]";

    }
}

