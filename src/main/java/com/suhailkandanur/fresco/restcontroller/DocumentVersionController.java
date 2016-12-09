package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Map;

/**
 * Created by suhail on 2016-12-08.
 */
@RestController
@CrossOrigin
public class DocumentVersionController {

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
        throw new NotImplementedException();
    }

    @PostMapping("/documentversion/{storeId}/{docId}")
    public DocumentVersion createDocumentVersion(@PathVariable String storeId, @PathVariable String docId, @RequestBody Map<String, String> params) {
        throw new NotImplementedException();
    }

}


