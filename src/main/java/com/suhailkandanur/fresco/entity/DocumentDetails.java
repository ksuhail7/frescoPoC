package com.suhailkandanur.fresco.entity;

import java.util.List;

/**
 * Created by suhail on 2016-12-16.
 */
public class DocumentDetails {
    private String documentId;
    private String storeId;
    private int versionCount;
    private List<Long> versions;

    public DocumentDetails(String storeId, String documentId) {
        this.documentId = documentId;
        this.storeId = storeId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getVersionCount() {
        return versionCount;
    }

    public void setVersionCount(int versionCount) {
        this.versionCount = versionCount;
    }

    public List<Long> getVersions() {
        return versions;
    }

    public void setVersions(List<Long> versions) {
        this.versions = versions;
    }
}
