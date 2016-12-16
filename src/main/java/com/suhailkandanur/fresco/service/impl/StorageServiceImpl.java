package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.dataaccess.DocumentVersionRepository;
import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.dataaccess.StoreRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.Repository;
import com.suhailkandanur.fresco.entity.Store;
import com.suhailkandanur.fresco.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

/**
 * Created by suhail on 2016-12-09.
 */
@Service
public class StorageServiceImpl implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
    @Autowired
    private FrescoRepoRepository frescoRepoRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Autowired
    private FrescoConfiguration frescoConfiguration;

    @Override
    public String getRootPath(Repository repository) {
        String rootPath = null;
        if (repository != null && repository.getId() != null) {
            Repository repo = frescoRepoRepository.findOne(repository.getId());
            rootPath = (repo == null) ? null : repo.getRootPath();
        }
        if (rootPath == null)
            rootPath = Paths.get(frescoConfiguration.getFileSystem(), "fresco").toString();
        return rootPath;
    }

    @Override
    public String getRootPath(Store store) {
        if (store == null) {
            return null;
        }
        String repositoryId = store.getRepositoryId();
        Repository repo = frescoRepoRepository.findOne(repositoryId);
        return repo == null ? null : Paths.get(repo.getRootPath(), "stores").toString();
    }

    @Override
    public String getRootPath(Document document) {
        if (document == null)
            return null;
        Store store = storeRepository.findOne(document.getStoreId());
        return Paths.get(getRootPath(store), document.getStoreId(), "documents").toString();
    }

    @Override
    public String getObjectsRootPath(String storeId) {
        String rootPath = getRootPathForStoreId(storeId);
        return rootPath == null ? null : Paths.get(rootPath, "objects").toString();
    }

    @Override
    public String getDocumentsRootPath(String storeId) {
        String rootPath = getRootPathForStoreId(storeId);
        return rootPath == null ? null : Paths.get(rootPath, "documents").toString();
    }

    private String getRootPathForStoreId(String storeId) {
        if (storeId == null || "".equals(storeId)) {
            logger.info("store id is null");
            return null;
        }
        Store store = storeRepository.findOne(storeId);
        if (store == null) {
            logger.error("store not found for id '{}'", storeId);
            return null;
        }
        return getRootPath(store);
    }
}
