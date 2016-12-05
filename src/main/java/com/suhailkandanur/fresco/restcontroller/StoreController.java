package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.dataaccess.StoreRepository;
import com.suhailkandanur.fresco.entity.Store;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by suhail on 2016-12-04.
 */
@CrossOrigin
@RestController
public class StoreController {
    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/stores")
    public List<Store> getAllStoreList() {
        return storeRepository.findAll();
    }

    @PostMapping("/store")
    public Store createStore(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String description = params.get("description");
        String repositoryRefToken = params.get("repositoryRefToken");
        int repositoryRef = Integer.valueOf(params.getOrDefault("repositoryRef", "-1"));
        Objects.requireNonNull(name);
        if(repositoryRefToken == null && repositoryRef < 0) {
            logger.error("repository must be specified");
            return null;
        }
        String storeRefToken = UUID.randomUUID().toString();
        Store.StoreBuilder builder = new Store.StoreBuilder();
        Store store = builder.name(name).description(description).repositoryRef(repositoryRef).repositoryRefToken(repositoryRefToken).build();
        try {
            rabbitTemplate.convertAndSend("fresco", "store", JsonUtils.convertObjectToJsonStr(store));
            return store;
        } catch(IOException ioe) {
            logger.error("unable to send request for store creation, error: {}", ioe.getMessage());
        }
        return null;
    }
}
