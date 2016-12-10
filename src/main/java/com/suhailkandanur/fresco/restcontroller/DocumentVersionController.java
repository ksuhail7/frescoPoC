package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import com.suhailkandanur.fresco.util.ChecksumUtils;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by suhail on 2016-12-08.
 */
@RestController
@CrossOrigin
public class DocumentVersionController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentVersionController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FrescoConfiguration configuration;

    @GetMapping("/documentversion/{storeId}/{docId}/latest")
    public DocumentVersion getLatestDocumentVersion(@PathVariable String storeId, @PathVariable String docId) {
        throw new NotImplementedException();
    }

    @GetMapping("/documentversion/{storeId}/{docId}/{version}")
    public DocumentVersion getDocumentVersion(@PathVariable String storeId, @PathVariable String docId, @PathVariable long version) {
        throw new NotImplementedException();
    }

    @GetMapping("/documentversion/{storeId}/{docId}")
    public List<DocumentVersion> getDocumentVersions(String storeId, String docId) {
        throw new NotImplementedException();
    }

    @PostMapping("/documentversion/{storeId}/{docId}/upload")
    public DocumentVersion createDocumentVersion(@PathVariable String storeId, @PathVariable String docId, @RequestParam("file") MultipartFile file,
                                                 RedirectAttributes redirectAttributes) {
        logger.info("document version creation (post request with file upload) handler entry point");
        try {
            File tempFile = File.createTempFile("fresco", ".bin", new File(configuration.getTempStagingDirection()));
            logger.info("storing the uploaded file at temp staging location '{}'", tempFile.getAbsolutePath());
            Files.copy(file.getInputStream(), Paths.get(tempFile.getAbsolutePath()));
            DocumentVersion documentVersion = new DocumentVersion();
            documentVersion.setDocumentId(docId);
            documentVersion.setStoreId(storeId);
            documentVersion.setFilename(file.getName());
            logger.info("sending message to 'fresco' exchange for async processing");
            rabbitTemplate.convertAndSend("fresco",
                    "documentversion",
                    JsonUtils.convertObjectToJsonStr(documentVersion));
            return documentVersion;
        } catch (IOException ioe) {
            logger.error("unable to handle uploaded file content, error: {}", ioe.getMessage());
        }
        return null;
    }

    @PostMapping("/documentversion/{storeId}/{docId}")
    public DocumentVersion createDocumentVersion(@PathVariable String storeId, @PathVariable String docId, @RequestBody Map<String, String> params) {
        logger.info("document version creation (via path) handler entry point");
        if (params == null) {
            logger.error("no params specified, cannot create document version");
            return null;
        }
        return new DocumentVersion();
    }

}


