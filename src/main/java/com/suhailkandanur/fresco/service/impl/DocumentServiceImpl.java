package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.dataaccess.DocumentVersionRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.DocumentDetails;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import com.suhailkandanur.fresco.entity.FileObjectReference;
import com.suhailkandanur.fresco.service.DocumentService;
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
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by suhail on 2016-12-09.
 */
@Service
public class DocumentServiceImpl implements DocumentService {

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
        Map<String, String> requestParams = JsonUtils.convertStrToJson(message, Map.class);
        if (requestParams == null || requestParams.get("documentId") == null || requestParams.get("storeId") == null) {
            logger.error("required parameters not supplied with request, skipping document creation");
            return;
        }
        String storeId = requestParams.get("storeId");
        String docId = requestParams.get("documentId");
        Document found = documentRepository.findDocumentByStoreIdAndDocumentId(storeId, docId);
        if (found != null) {
            //document already exists, cannot create a new one
            logger.error("document with id '{}' already exists in store '{}', cannot create a new document. do you intend to update?", docId, storeId);
            return;
        }

        handleDocumentVersionCreation(requestParams, true);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-document-upd-request", durable = "true"),
            exchange = @Exchange(value = "fresco", type = "direct"), key = "document-update"))
    public void processUpdateRequest(String message) throws IOException {
        logger.info("received document update request, message: {}", message);
        Map<String, String> requestParams = JsonUtils.convertStrToJson(message, Map.class);
        if (requestParams == null || requestParams.get("documentId") == null || requestParams.get("storeId") == null) {
            logger.error("unable to construct document object from request, cannot update document");
            return;
        }
        String storeId = requestParams.get("storeId");
        String documentId = requestParams.get("documentId");
        Document existing = documentRepository.findDocumentByStoreIdAndDocumentId(storeId, documentId);
        if (existing == null) {
            logger.error("document not found for given store id {} and docid {}, cannot update the document, do you intend to create a new one?",
                    storeId, documentId);
            return;
        }
        handleDocumentVersionCreation(requestParams, false);
    }

    private void handleDocumentVersionCreation(Map<String, String> requestParams, boolean createNew) throws IOException {
        String docId = requestParams.get("documentId");
        String storeId = requestParams.get("storeId");
        String docIdSha1 = ChecksumUtils.sha1(docId);
        String token = requestParams.get("token");

        Document documentObj = new Document(storeId, docId, docIdSha1, token);

        if (createNew) {
            boolean status = initializeDocumentStorage(documentObj);
            if (!status) {
                //document storage not initialized
                logger.error("unable to initialize document storage, cannot proceed");
                return;
            }
        }
        String fileLocation = requestParams.get("fileLocation");
        String fileName = requestParams.get("fileName");
        Path filePath = Paths.get(fileLocation);
        String sha1 = ChecksumUtils.sha1(filePath);
        String mimeType = FileUtils.fileMimeType(filePath);
        long version = Long.valueOf(requestParams.get("version"));

        DocumentVersion documentVersionObj = new DocumentVersion();
        documentVersionObj.setFilename(fileName);
        documentVersionObj.setSha1(sha1);
        documentVersionObj.setDocumentId(docId);
        documentVersionObj.setStoreId(storeId);
        documentVersionObj.setMimetype(mimeType);
        documentVersionObj.setVersion(version);
        documentVersionObj.setFilesize(Files.size(filePath));
        boolean status = createDocumentVersionOnStorage(documentVersionObj, filePath);
        if (!status) {
            logger.error("cannot create document on filesystem, document create request failed");
            return;
        }
        saveToDatabase(documentObj, documentVersionObj, createNew);
    }

    private boolean initializeDocumentStorage(Document document) {
        if (document == null || document.getStoreId() == null || document.getDocumentId() == null) {
            logger.error("document is null, cannot initialize storage");
            return false;
        }
        String rootPathStr = storageService.getDocumentsRootPath(document.getStoreId());
        Path rootPath = Paths.get(rootPathStr);
        if (Files.notExists(rootPath)) {
            logger.error("document root path '{}' does not exists, cannot create document", rootPath);
            return false;
        }
        String docIdSha1 = document.getDocIdSha1();
        Path documentPath = rootPath.resolve(docIdSha1.substring(0, 2))
                .resolve(docIdSha1.substring(2, 6))
                .resolve(docIdSha1.substring(6));
        if (Files.exists(documentPath)) {
            logger.info("document path '{}' already exists, skipping creation", documentPath);
            return false;
        }
        logger.info("creating document path: {}", documentPath);
        try {
            Files.createDirectories(documentPath);
            FileUtils.writeMetaInfFile(documentPath, document);
            return true;
        } catch (IOException ioe) {
            logger.error("unable to initialize storage for document under '{}' ,  error: {}", documentPath, ioe.getMessage());
            return false;
        }
    }

    @Transactional
    private void saveToDatabase(Document document, DocumentVersion documentVersion, boolean createNew) {
        DocumentVersion savedVersion = documentVersionRepository.save(documentVersion);
        Document savedDocument = null;
        if (createNew) {
            savedDocument = documentRepository.save(document);
        }
        if ((createNew && savedDocument == null) || savedVersion == null) {
            throw new DataRetrievalFailureException("unable to save document and its corresponding version in database, transaction will be rolled back");
        }
    }

    private boolean createDocumentVersionOnStorage(DocumentVersion documentVersion, Path sourcePath) throws IOException {
        String rootPath = storageService.getObjectsRootPath(documentVersion.getStoreId());
        String sha1 = documentVersion.getSha1();
        Path objectPath = Paths.get(rootPath)
                .resolve(sha1.substring(0, 2))
                .resolve(sha1.substring(2, 6))
                .resolve(sha1.substring(6));
        if (Files.exists(objectPath)) {
            logger.info("file object already exists at '{}', skipping file creation", objectPath);
        } else {

            try {
                //create parent directories
                Path parentDir = objectPath.getParent();
                if (Files.notExists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                Files.copy(sourcePath, objectPath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("the file object is saved at '{}'", objectPath);
            } catch (IOException e) {
                logger.info("unable to copy file to objects folder, error:  {} ", e.getMessage());
                e.printStackTrace();
                return false;
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
        logger.debug("creating version file '{}'", versionPath);

        if (Files.exists(versionPath)) {
            //this cannot happen
            logger.error("FATAL: version already exists, this case should not happen");
            return false;
        }
        Path parentPath = versionPath.getParent();
        if (Files.notExists(parentPath)) Files.createDirectories(parentPath);
        FileUtils.writeToFile(versionPath, JsonUtils.convertObjectToJsonStr(documentVersion));
        logger.info("created the version file '{}'", versionPath);
        return true;
    }

    @Override
    public Document findDocumentByStoreIdAndDocumentIdAndVersion(String storeId, String docId, long version) {
        throw new NotImplementedException();
    }

    @Override
    public DocumentDetails getDocumentDetails(String storeId, String docId) {
        Document document = documentRepository.findDocumentByStoreIdAndDocumentId(storeId, docId);
        List<DocumentVersion> documentVersions = documentVersionRepository.findDocumentVersionByStoreIdAndDocumentId(storeId, docId);
        if (document != null && documentVersions != null) {
            DocumentDetails details = new DocumentDetails(storeId, docId);
            details.setVersionCount(documentVersions.size());
            details.setVersions(documentVersions.stream()
                    .map(DocumentVersion::getVersion)
                    .collect(Collectors.toList()));
            return details;
        } else {
            logger.error("no document found for store id {} and doc id {}", storeId, docId);
        }
        return null;
    }

    @Override
    public DocumentVersion getDocumentVersionDetails(String storeId, String docId, long version) {
        return documentVersionRepository.findDocumentVersionByStoreIdAndDocumentIdAndVersion(storeId, docId, version);
    }

    @Override
    public FileObjectReference getFileObjectReference(String storeId, String docId) {
        List<DocumentVersion> documentVersions = documentVersionRepository.findDocumentVersionByStoreIdAndDocumentId(storeId, docId);
        if (documentVersions != null && documentVersions.size() > 0) {
            //find the latest version
            DocumentVersion versionLatest = documentVersions.stream()
                    .max(Comparator.comparing(DocumentVersion::getVersion))
                    .get();
            return getFileObjectReference(versionLatest);
        }
        return null;
    }

    @Override
    public FileObjectReference getFileObjectReference(String storeId, String docId, long version) {
        DocumentVersion documentVersion = documentVersionRepository.findDocumentVersionByStoreIdAndDocumentIdAndVersion(storeId, docId, version);
        return getFileObjectReference(documentVersion);
    }

    private FileObjectReference getFileObjectReference(DocumentVersion documentVersion) {
        if (documentVersion != null) {
            String mimeType = documentVersion.getMimetype();
            String objectsRootPath = storageService.getObjectsRootPath(documentVersion.getStoreId());
            String sha1 = documentVersion.getSha1();
            String fileLocation = Paths.get(objectsRootPath, sha1.substring(0, 2), sha1.substring(2, 6), sha1.substring(6))
                    .toString();
            FileObjectReference reference = new FileObjectReference(fileLocation, mimeType);
            return reference;
        } else {
            logger.error("unable to determine the requested file version");
        }
        return null;
    }
}
