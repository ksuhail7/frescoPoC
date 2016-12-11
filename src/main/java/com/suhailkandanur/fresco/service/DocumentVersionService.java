package com.suhailkandanur.fresco.service;

import com.suhailkandanur.fresco.entity.DocumentVersion;

import java.util.List;

/**
 * Created by suhail on 2016-12-10.
 */
public interface DocumentVersionService {
    DocumentVersion getLatestDocumentVersion(String storeId, String docId);
    DocumentVersion getDocumentVersion(String storeId, String docId, long version);
    List<DocumentVersion> getDocumentVersions(String storeId, String docId);
}
