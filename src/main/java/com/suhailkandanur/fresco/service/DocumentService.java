package com.suhailkandanur.fresco.service;

import com.suhailkandanur.fresco.entity.Document;

/**
 * Created by suhail on 2016-12-08.
 */
public interface DocumentService {
    Document findDocumentByStoreIdAndDocumentIdAndVersion(String storeId, String docId, long version);
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
