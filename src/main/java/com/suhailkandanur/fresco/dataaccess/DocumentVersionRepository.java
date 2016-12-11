package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.DocumentVersion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by suhail on 2016-12-05.
 */
public interface DocumentVersionRepository extends MongoRepository<DocumentVersion, String> {
    DocumentVersion findDocumentVersionByStoreIdAndDocumentIdAndVersion(String storeId, String docId, long version);
    List<DocumentVersion> findDocumentVersionByStoreIdAndDocumentId(String storeId, String docId);
}
