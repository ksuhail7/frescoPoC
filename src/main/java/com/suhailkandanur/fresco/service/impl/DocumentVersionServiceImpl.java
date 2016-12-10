package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentVersionRepository;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import com.suhailkandanur.fresco.service.RabbitQueueListener;
import com.suhailkandanur.fresco.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by suhail on 2016-12-09.
 */
public class DocumentVersionServiceImpl implements RabbitQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(DocumentVersionServiceImpl.class);

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private StorageService storageService;

    @Override
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-documentversion-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "documentversion"))
    public void processMessage(String message) throws Exception {
        logger.info("received request to create document version");
    }


    private void saveDocumentVersionToDatabase(DocumentVersion documentVersion) {
        documentVersionRepository.save(documentVersion);
    }
}
