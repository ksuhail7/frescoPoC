package com.suhailkandanur.fresco.restcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by suhail on 2016-12-01.
 */
@RestController
@Component(value = "frescoRepoController")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/repository")
    public String createRepository(String name, String description, long quota) {
        logger.info("received repository creation request [name: {}, description: {}, quota: {}", name, description, quota);
        String token = UUID.randomUUID().toString();

        return token;
    }

}
