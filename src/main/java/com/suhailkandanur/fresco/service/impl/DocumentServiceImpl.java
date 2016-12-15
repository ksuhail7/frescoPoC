package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.dataaccess.DocumentVersionRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.DocumentVersion;
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
public class DocumentServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-document-cr-request", durable = "true"),
            exchange = @Exchange(value = "fresco", type = "direct"), key = "document-create"))
    public void processCreateRequest(String message) throws Exception {
        logger.info("received request to create document {}", message);
        Document doc = JsonUtils.convertStrToJson(message, Document.class);
        if (doc == null || doc.getDocumentId() == null || doc.getStoreId() == null) {
            logger.error("cannot construct document object from request, skipping document creation");
            return;
        }
        Document found = documentRepository.findDocumentByStoreIdAndDocumentId(doc.getStoreId(), doc.getDocumentId());
        if (found != null) {
            //document already exists, cannot create a new one
            logger.error("document with id '{}' already exists in store '{}', cannot create a new document. do you intend to update?", doc.getStoreId(), doc.getDocumentId());
            return;
        }

        DocumentVersion version = initializeDocumentStorage(doc);
        if (version == null) {
            logger.error("cannot create document on filesystem, document create request failed");
            return;
        }
        saveToDatabase(doc, version);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-document-upd-request", durable = "true"),
            exchange = @Exchange(value = "fresco", type = "direct"), key = "document-update"))
    public void processUpdateRequest(String message) throws IOException {
        logger.info("received document update request, message: {}", message);
        Document doc = JsonUtils.convertStrToJson(message, Document.class);
        if (doc == null || doc.getDocumentId() == null || doc.getStoreId() == null) {
            logger.error("unable to construct document object from request, cannot update document");
            return;
        }
        Document existing = documentRepository.findOne(doc.getDocumentId());
        if (existing == null || !doc.getStoreId().equals(existing.getStoreId())) {
            logger.error("document not found for given store id {} and docid {}", doc.getDocumentId(), doc.getStoreId());
            return;
        }

    }

    private DocumentVersion initializeDocumentStorage(Document doc) {
        if (doc == null) {
            logger.error("document is null, cannot initialize storage");
             return null;
        }
        String rootPathStr = storageService.getRootPath(doc);
        Path rootPath = Paths.get(rootPathStr);
        if (Files.notExists(rootPath)) {
            logger.error("document root path '{}' does not exists, cannot create document", rootPath);
            return null;
        }
        String docIdSha1 = doc.getDocIdSha1();
        Path documentPath = rootPath.resolve(docIdSha1.substring(0, 2))
                .resolve(docIdSha1.substring(2, 6))
                .resolve(docIdSha1.substring(6));
        if (Files.exists(documentPath)) {
            logger.info("document path '{}' already exists, skipping creation", documentPath);
            return null;
        }
        logger.info("creating document path: {}", documentPath);
        try {
            Files.createDirectories(documentPath);
            FileUtils.writeMetaInfFile(documentPath, doc);
        } catch (IOException ioe) {
            logger.error("unable to initialize storage for document under '{}' ,  error: {}", documentPath, ioe.getMessage());
            return null;
        }
        return null;
    }

    private void saveToDatabase(Document document, DocumentVersion documentVersion) {
        documentVersionRepository.save(documentVersion);
        documentRepository.save(document);
    }
}
