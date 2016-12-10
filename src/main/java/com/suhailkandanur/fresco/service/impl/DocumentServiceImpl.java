package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.entity.Document;
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
 * Created by suhail on 2016-12-09.
 */
@Service
public class DocumentServiceImpl implements RabbitQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-document-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "document"))
    public void processMessage(String message) throws Exception {
        logger.info("received request to create document {}", message);
        Document doc = JsonUtils.convertStrToJson(message, Document.class);
        saveToDatabase(doc);
    }

    private void createDocumentStorage(Document doc) {

    }

    private void saveToDatabase(Document document) {
        documentRepository.save(document);
    }
}
