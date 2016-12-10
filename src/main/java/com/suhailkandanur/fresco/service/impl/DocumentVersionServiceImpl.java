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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
        createDocumentVersionOnStorage(documentVersion, filePath);
        saveDocumentVersionToDatabase(documentVersion);
    }

    private void createDocumentVersionOnStorage(DocumentVersion documentVersion, Path sourcePath) throws IOException {
        String rootPath = storageService.getObjectsRootPath(documentVersion.getStoreId());
        logger.info("root path for writing document version");
        String sha1 = documentVersion.getSha1();
        Path objectPath = Paths.get(rootPath)
                .resolve(sha1.substring(0, 2))
                .resolve(sha1.substring(2, 6))
                .resolve(sha1.substring(6));
        if (Files.exists(objectPath)) {
            logger.info("file object already exists at '{}'", objectPath);
        } else {

            try {
                //create parent directories
                Path parentDir = objectPath.getParent();
                if (Files.notExists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                Files.copy(sourcePath, objectPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.info("unable to copy file to objects folder, error:  {} ", e.getMessage());
                e.printStackTrace();
            }
        }

        //create the version
        String documentRootPath = storageService.getDocumentsRootPath(documentVersion.getStoreId());
        String docIdSha1 = ChecksumUtils.sha1(documentVersion.getDocumentId());
        Path versionPath = Paths.get(documentRootPath)
                .resolve(docIdSha1.substring(0, 2))
                .resolve(docIdSha1.substring(2, 6))
                .resolve(docIdSha1.substring(6))
                .resolve(Long.toString(documentVersion.getVersion()));

        if (Files.exists(versionPath)) {
            //this cannot happen
            logger.error("version already exists, this case should not happen");
            return;
        }
        Path parentPath = versionPath.getParent();
        if(Files.notExists(parentPath)) Files.createDirectories(parentPath);
        FileUtils.writeToFile(versionPath, JsonUtils.convertObjectToJsonStr(documentVersion));
    }


    private void saveDocumentVersionToDatabase(DocumentVersion documentVersion) {
        documentVersionRepository.save(documentVersion);
    }
}
