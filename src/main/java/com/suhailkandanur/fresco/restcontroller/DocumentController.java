package com.suhailkandanur.fresco.restcontroller;

import com.suhailkandanur.fresco.entity.Document;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.naming.NoInitialContextException;
import java.util.List;
import java.util.Map;

/**
 * Created by suhail on 2016-12-06.
 */
@RestController
@CrossOrigin
public class DocumentController {

    @GetMapping("/document/{id}")
    public Document getDocument(@PathVariable String docId) {
        throw new NotImplementedException();
    }

    @PostMapping("/document")
    public Document createDocument(@RequestBody Map<String, String> request) {
        throw new NotImplementedException();
    }
}
