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

    private String token;

    public Document() {}

    public Document(final String storeId, final String docId, final String docIdSha1, String token) {
        this.storeId = storeId;
        this.documentId = docId;
        this.docIdSha1 = docIdSha1;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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

    private static final class DocumentBuilder {
        private String documentId, docIdSha1, storeId, token;
        public DocumentBuilder() {}
        public Document build() {
            return new Document(storeId, documentId, docIdSha1, token);
        }

        public DocumentBuilder storeId(String storeId) {
            this.storeId = storeId;
            return this;
        }

        public DocumentBuilder documentId(String docId) {
            this.documentId = docId;
            return this;
        }

        public DocumentBuilder docIdSha1(String docIdSha1) {
            this.docIdSha1 = docIdSha1;
            return this;
        }

        public DocumentBuilder token(String token) {
            this.token = token;
            return this;
        }

    }
}
