package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.DocumentRepository;
import com.suhailkandanur.fresco.dataaccess.DocumentVersionRepository;
import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.dataaccess.StoreRepository;
import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.Repository;
import com.suhailkandanur.fresco.entity.Store;
import com.suhailkandanur.fresco.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

/**
 * Created by suhail on 2016-12-09.
 */
@Service
public class StorageServiceImpl implements StorageService {
    @Autowired
    private FrescoRepoRepository frescoRepoRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentVersionRepository documentVersionRepository;

    @Override
    public String getRootPath(Repository repository) {
        if(repository == null) {
            return null;
        }
        Repository repo = frescoRepoRepository.findOne(repository.getId());
        return repo == null ? null : repo.getRootPath();
    }

    @Override
    public String getRootPath(Store store) {
        if(store == null) {
            return null;
        }
        String repositoryId = store.getRepositoryId();
        Repository repo = frescoRepoRepository.findOne(repositoryId);
        return repo == null ? null : Paths.get(repo.getRootPath(), "stores").toString();
    }

    @Override
    public String getRootPath(Document document) {
        if(document == null)
            return null;
        Store store = storeRepository.findOne(document.getStoreId());
        return Paths.get(getRootPath(store), document.getStoreId(), "documents").toString();
    }

    @Override
    public String getObjectsRootPath(Store store) {
        String rootPath = getRootPath(store);
        return rootPath == null ? null : Paths.get(rootPath, "objects").toString();
    }
}
