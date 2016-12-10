package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentVersionRepository;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import com.suhailkandanur.fresco.service.RabbitQueueListener;
import com.suhailkandanur.fresco.service.StorageService;
import com.suhailkandanur.fresco.util.ChecksumUtils;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suhail on 2016-12-09.
 */
@Service
public class DocumentVersionServiceImpl implements RabbitQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(DocumentVersionServiceImpl.class);

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private StorageService storageService;

    @Override
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-documentversion-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "documentversion"))
    public void processMessage(String message) throws Exception {
        if(message == null) {
            logger.error("request is null");
            return;
        }
        logger.info("received request to create document version, request: {}", message);
        Map<String, String> requestParams = JsonUtils.convertStrToJson(message, HashMap.class);

        String fileLocation = requestParams.get("fileLocation");
        Path filePath = Paths.get(fileLocation);
        String fileName = requestParams.get("fileName");
        String token = requestParams.get("token");
        String docId = requestParams.get("docId");
        String storeId = requestParams.get("storeId");
        long version = Long.valueOf(requestParams.get("version"));

        String sha1 = ChecksumUtils.sha1(filePath);
        String mimeType = FileUtils.fileMimeType(filePath);

        DocumentVersion documentVersion = new DocumentVersion();
        documentVersion.setFilename(fileName);
        documentVersion.setSha1(sha1);
        documentVersion.setDocumentId(docId);
        documentVersion.setStoreId(storeId);
        documentVersion.setMimetype(mimeType);
        documentVersion.setVersion(version);
        documentVersion.setFilesize(Files.size(filePath));
        createDocumentVersiononStorage(documentVersion);
        saveDocumentVersionToDatabase(documentVersion);
    }

    private void createDocumentVersiononStorage(DocumentVersion documentVersion) {
        String rootPath = storageService.getRootPath(documentVersion);
        //TODO: pending implementation
    }


    private void saveDocumentVersionToDatabase(DocumentVersion documentVersion) {
        documentVersionRepository.save(documentVersion);
    }
}
