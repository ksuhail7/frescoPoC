package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.DocumentDetails;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import com.suhailkandanur.fresco.entity.FileObjectReference;
import com.suhailkandanur.fresco.service.DocumentService;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by suhail on 2016-12-06.
 */
@RestController(value = "frescoDocumentController")
@CrossOrigin
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private FrescoConfiguration configuration;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/document/{storeId}/{docId}")
    public DocumentDetails getDocumentDetails(@PathVariable String storeId, @PathVariable String docId) {
        logger.debug("getting document details for doc id {} in store {}", docId, storeId);
        return documentService.getDocumentDetails(storeId, docId);
    }

    @GetMapping("/document/{storeId}/{docId}/{version}")
    public DocumentVersion getDocumentVersionDetails(@PathVariable String storeId, @PathVariable String docId, @PathVariable long version) {
        return documentService.getDocumentVersionDetails(storeId, docId, version);
    }

    @GetMapping("/document/{storeId}")
    public List<Document> getDocumentsInStore(@PathVariable String storeId) {
        return documentRepository.findDocumentByStoreId(storeId);
    }

    @GetMapping("/document/{storeId}/{docId}/retrieve")
    @ResponseBody
    public ResponseEntity<Resource> retrieveLatestDocument(@PathVariable String storeId, @PathVariable String docId, HttpServletResponse response) throws IOException {
        FileObjectReference fileObjectReference = documentService.getFileObjectReference(storeId, docId);
        return streamDocument(fileObjectReference, response);

    }

    @GetMapping("/document/{storeId}/{docId}/{version}/retrieve")
    @ResponseBody
    public ResponseEntity<Resource> retrieveDocumentVersion(@PathVariable String storeId, @PathVariable String docId, @PathVariable long version, HttpServletResponse response) throws IOException {
        FileObjectReference fileObjectReference = documentService.getFileObjectReference(storeId, docId, version);
        return streamDocument(fileObjectReference, response);
    }

    private ResponseEntity<Resource> streamDocument(FileObjectReference reference, HttpServletResponse response) throws IOException {
        if (reference == null) {
            String msg = "requested file not found/cannot be served";
            logger.error(msg);
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
            return null;
        }
        Path filePath = Paths.get(reference.getFileLocation());
        Resource fileResource = new UrlResource(filePath.toUri());
        try {
            if (fileResource != null && fileResource.exists() && fileResource.isReadable()) {
                return ResponseEntity
                        .ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, reference.getMimeType())
                        .body(fileResource);
            } else {
                logger.error("unable to read file {}", reference.getFileLocation());
                response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "unable to read file for streaming");
                return null;
            }
        } catch (IOException e) {
            logger.error("unable to stream file, exception: {}", e.getMessage());
        } finally {

        }
        return null;
    }

    @PostMapping(value = "/document/{storeId}/{docId}/upload", headers = "content-type=multipart/form-data")
    public Map<String, String> createDocument(@PathVariable String storeId, @PathVariable String docId, @RequestParam("file") MultipartFile file,
                                              RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
        logger.info("document version creation (post request with file upload) handler entry point");
        //input validation
        try {
            return handleFileUpload(storeId, docId, file, "document-create");
        } catch (IOException ioe) {
            logger.error("unable to handle uploaded file content, error: {}", ioe.getMessage());
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Unable to document creation request");
            ioe.printStackTrace();
        } catch (IllegalArgumentException iae) {
            String msg = "required parameters storeid, docid and file not specified, cannot create document";
            logger.error(msg);
            response.sendError(HttpStatus.SC_BAD_REQUEST, msg);
        }
        return null;
    }

    /**
     * @param storeId            the store id
     * @param docId              the document id
     * @param file               the uploaded file
     * @param redirectAttributes attributes
     * @return the response with token
     */
    @PutMapping(value = "/document/{storeId}/{docId}/upload", headers = "content-type=multipart/form-data")
    public Map<String, String> updateDocument(@PathVariable String storeId, @PathVariable String docId, @RequestParam("file") MultipartFile file,
                                              RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
        logger.info("document version update (post request with file upload) handler entry point");
        //input validation
        try {
            return handleFileUpload(storeId, docId, file, "document-update");
        } catch (IOException ioe) {
            logger.error("unable to handle uploaded file content, error: {}", ioe.getMessage());
            response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Unable to handle document update request");
            ioe.printStackTrace();
        } catch (IllegalArgumentException iae) {
            String msg = "required parameters storeid, docid and file not specified, cannot update document";
            logger.error(msg);
            response.sendError(HttpStatus.SC_BAD_REQUEST, msg);
        }
        return null;
    }

    private Map<String, String> handleFileUpload(String storeId, String docId, MultipartFile file, String routingKey) throws IOException, IllegalArgumentException {
        if (storeId == null || docId == null || file == null) {
            String msg = "required parameters storeid, docid and file not specified, cannot create document";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        String stagingLocation = configuration.getTempStagingDirection();
        Path stagingPath = Paths.get(stagingLocation);
        if (Files.notExists(stagingPath)) {
            logger.info("staging directory '{}' does not exist, attempting to create one", stagingLocation);
            Files.createDirectories(stagingPath);
        }
        Path outputFile = Files.createTempFile(stagingPath, "fresco-", ".bin");
        Files.copy(file.getInputStream(), outputFile, StandardCopyOption.REPLACE_EXISTING);
        String token = UUID.randomUUID().toString();
        Map<String, String> requestParamsMap = new HashMap<>();
        requestParamsMap.put("storeId", storeId);
        requestParamsMap.put("documentId", docId);
        requestParamsMap.put("fileLocation", outputFile.toString());
        requestParamsMap.put("fileName", file.getOriginalFilename());
        requestParamsMap.put("token", token);
        String version = Long.toString(System.currentTimeMillis());
        requestParamsMap.put("version", version);
        logger.info("sending message to 'fresco' exchange for async processing");
        rabbitTemplate.convertAndSend("fresco",
                routingKey,
                JsonUtils.convertObjectToJsonStr(requestParamsMap));
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("storeId", storeId);
        responseObj.put("documentId", docId);
        responseObj.put("token", token);
        responseObj.put("version", version);
        responseObj.put("fileName", file.getOriginalFilename());
        return responseObj;
    }


}
