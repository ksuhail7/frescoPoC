package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.dataaccess.StoreRepository;
import com.suhailkandanur.fresco.entity.Repository;
import com.suhailkandanur.fresco.entity.Store;
import com.suhailkandanur.fresco.service.StorageService;
import com.suhailkandanur.fresco.util.FileUtils;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by suhail on 2016-12-04.
 */
@Service
public class StoreServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FrescoRepoRepository repoRepository;

    @Autowired
    private StorageService storageService;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-store-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "store"))
    public void processMessage(String data) {
        try {
            Store store = JsonUtils.convertStrToJson(data, Store.class);
            logger.info("received request: {}, store object: {}", data, store.getName());
            if (store.getRepositoryId() == null) {
                logger.error("repository id not specified, cannot create store");
                return;
            }
            Repository repository = repoRepository.findOne(store.getRepositoryId());
            if (repository == null) {
                logger.error("unable to find repository with id '{}'", store.getRepositoryId());
                return;
            }
            initializeStoreStorage(store);
            writeEntryToDatabase(store);
        } catch (IOException ioe) {
            return;
        }
    }

    public void initializeStoreStorage(Store store) throws IOException {
        String rootPathStr = storageService.getRootPath(store);
        Path storeRootPath = Paths.get(rootPathStr);
        if (Files.notExists(storeRootPath)) {
            logger.error("repository path '{}' does not exists, cannot create store", storeRootPath);
            throw new IOException("repository path does not exists");
        }

        Path storePath = storeRootPath.resolve(store.getName());
        if (Files.exists(storePath)) {
            logger.error("store already exists at path '{}', cannot create a new one", storePath);
            throw new IOException("store already exists");
        }
        Files.createDirectories(storePath);

        Path metaInfFile = FileUtils.writeMetaInfFile(storePath, store);
        //just make sure the meta.inf file is created
        if (Files.notExists(metaInfFile)) {
            logger.error("store meta.inf file not created for unknown reasons");
            throw new IOException("unable to create meta.inf file for store");
        }

        Path documentsRootPath = storePath.resolve("documents");
        Files.createDirectories(documentsRootPath);
        Path objectsRootPath = storePath.resolve("objects");
        Files.createDirectories(objectsRootPath);
    }

    public void writeEntryToDatabase(Store store) {
        storeRepository.save(store);
    }


}
