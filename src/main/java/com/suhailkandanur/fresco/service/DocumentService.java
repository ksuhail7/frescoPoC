package com.suhailkandanur.fresco.service;

import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.DocumentDetails;
import com.suhailkandanur.fresco.entity.DocumentVersion;
import com.suhailkandanur.fresco.entity.FileObjectReference;

/**
 * Created by suhail on 2016-12-08.
 */
public interface DocumentService {
    Document findDocumentByStoreIdAndDocumentIdAndVersion(String storeId, String docId, long version);

    DocumentDetails getDocumentDetails(String storeId, String docId);

    DocumentVersion getDocumentVersionDetails(String storeId, String docId, long version);

    FileObjectReference getFileObjectReference(String storeId, String docId);

    FileObjectReference getFileObjectReference(String storeId, String docId, long version);

//    Document getDocument(String storeId, String docId);
//
//    List<Document> getDocumentsInStore(String storeId);
//
//    File getDocumentFile(String storeId, String docId);
//
//    default File getDocumentFile(Document document) {
//        if (document != null)
//            return getDocumentFile(document.getStoreId(), document.getDocumentId());
//        return null;
//    }
//
//    File getDocumentFile(String storeId, String docId, long version);
//
//    default File getDocumentFile(Document document, long version) {
//        if(document != null)
//            return getDocumentFile(document.getStoreId(), document.getDocumentId(), version);
//        return null;
//    }


}
