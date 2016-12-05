package com.suhailkandanur.fresco.service.impl;

import com.rabbitmq.tools.json.JSONUtil;
import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.dataaccess.StoreRepository;
import com.suhailkandanur.fresco.entity.Store;
import com.suhailkandanur.fresco.service.RabbitQueueListener;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by suhail on 2016-12-04.
 */
@Service
public class StoreServiceImpl implements RabbitQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FrescoRepoRepository repoRepository;

    @Override
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-store-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "store"))
    public void processMessage(String data) throws Exception {
        logger.info("received request {}", data);
        Store store = JsonUtils.convertStrToJson(data, Store.class);
        logger.info("store object: {}", store.getName());
    }



}
