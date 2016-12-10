package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.service.DocumentService;
import com.suhailkandanur.fresco.util.ChecksumUtils;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.naming.NoInitialContextException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/document/{storeId}/{docId}")
    public Document getDocument(@PathVariable String storeId, @PathVariable String docId) {
        return documentRepository.findDocumentByStoreIdAndDocumentId(storeId, docId);
    }

    @PostMapping("/document")
    public Document createDocument(@RequestBody Map<String, String> request) throws IOException {
        logger.info("create document entry point");
        String docId = request.get("docId");
        String storeId = request.get("storeId");
        Document document = new Document();
        document.setDocumentId(docId);
        document.setStoreId(storeId);
        document.setDocIdSha1(ChecksumUtils.sha1(docId));
        String requestJson = JsonUtils.convertObjectToJsonStr(document);
        rabbitTemplate.convertAndSend("fresco", "document", requestJson);
        return document;
    }

    @GetMapping("/document/{storeId}")
    public List<Document> getDocumentsInStore(@PathVariable String storeId) {
        return documentRepository.findDocumentByStoreId(storeId);
    }

    @GetMapping("/document/{storeId}/{docId}/retrieve")
    public void retrieveFile(@PathVariable String storeId, @PathVariable String docId, HttpServletResponse response) {
        InputStream inputStream = null;
        try {
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch(IOException e) {
            logger.error("unable to stream file, exception: {}", e.getMessage());
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch(IOException ioe) {
                    logger.error("unable to close input stream, exception: {}", ioe.getMessage());
                }
            }
        }
    }
}
