package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.service.RabbitQueueListener;
import com.suhailkandanur.fresco.service.StorageService;
import com.suhailkandanur.fresco.util.FileUtils;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by suhail on 2016-12-09.
 */
@Service
public class DocumentServiceImpl implements RabbitQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-document-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "document"))
    public void processMessage(String message) throws Exception {
        logger.info("received request to create document {}", message);
        Document doc = JsonUtils.convertStrToJson(message, Document.class);
        initializeDocumentStorage(doc);
        saveToDatabase(doc);
    }

    private void initializeDocumentStorage(Document doc) {
        if (doc == null)
            return;
        String rootPathStr = storageService.getRootPath(doc);
        Path rootPath = Paths.get(rootPathStr);
        if (Files.notExists(rootPath)) {
            logger.error("document root path '{}' does not exists, cannot create document", rootPath);
            return;
        }
        String docIdSha1 = doc.getDocIdSha1();
        Path documentPath = rootPath.resolve(docIdSha1.substring(0, 2))
                .resolve(docIdSha1.substring(2, 6))
                .resolve(docIdSha1.substring(6));
        if (Files.exists(documentPath)) {
            logger.info("document path '{}' already exists, skipping creation", documentPath);
            return;
        }
        logger.info("creating document path: {}", documentPath);
        try {
            Files.createDirectories(documentPath);
            FileUtils.writeMetaInfFile(documentPath, doc);
        } catch (IOException ioe) {
            logger.error("unable to initialize storage for document under '{}' ,  error: {}", documentPath, ioe.getMessage());
            return;
        }
    }

    private void saveToDatabase(Document document) {
        documentRepository.save(document);
    }
}
