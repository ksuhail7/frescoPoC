package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by suhail on 2016-12-05.
 */
public interface DocumentRepository extends MongoRepository<Document, String> {
    List<Document> findDocumentByStoreIdAndDocumentId(String storeId, String docId);

    Document findDocumentByStoreIdAndDocumentIdAndVersion(String storeId, String docId, long version);
    List<Document> findDocumentByStoreId(String storeId);
}
