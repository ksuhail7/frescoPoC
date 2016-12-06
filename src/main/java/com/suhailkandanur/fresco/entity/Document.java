package com.suhailkandanur.fresco.entity;

import org.springframework.data.annotation.Id;

/**
 * Created by suhail on 2016-12-05.
 */
public class Document {
    @Id
    private String id;

    private String documentId;
    private String docIdSha1;

    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocIdSha1() {
        return docIdSha1;
    }

    public void setDocIdSha1(String docIdSha1) {
        this.docIdSha1 = docIdSha1;
    }
}
