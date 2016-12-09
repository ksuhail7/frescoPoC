package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.entity.Document;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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



    @GetMapping("/document/{storeId}/{docId}")
    public Document getDocument(@PathVariable String storeId, @PathVariable String docId) {
        throw new NotImplementedException();
    }

    @PostMapping("/document")
    public Document createDocument(@RequestBody Map<String, String> request) {
        throw new NotImplementedException();
    }

    @GetMapping("/document/{storeId}")
    public List<Document> getDocumentsInStore(@PathVariable String storeId) {
        throw new NotImplementedException();
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
