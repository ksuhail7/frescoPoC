package com.suhailkandanur.fresco.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by suhail on 2016-12-01.
 */
@RestController
public class WelcomeController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Fresco";
    }
}
