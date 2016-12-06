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

    @GetMapping("/store/{id}")
    public Store getStoreById(@PathVariable String id) {
        return storeRepository.findOne(id);
    }

    @PostMapping("/store")
    public Store createStore(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        String description = params.get("description");
        String repositoryId = params.getOrDefault("repositoryId", null);
        Objects.requireNonNull(name);
        if(repositoryId == null) {
            logger.error("repository must be specified");
            return null;
        }
        String storeRefToken = UUID.randomUUID().toString();
        Store store = new Store(name, description, repositoryId, storeRefToken);
        try {
            rabbitTemplate.convertAndSend("fresco", "store", JsonUtils.convertObjectToJsonStr(store));
            return store;
        } catch(IOException ioe) {
            logger.error("unable to send request for store creation, error: {}", ioe.getMessage());
        }
        return null;
    }
}
