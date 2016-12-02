package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.dataaccess.RepositoryDAO;
import com.suhailkandanur.fresco.entity.Repository;
import com.suhailkandanur.fresco.service.RepositoryService;
import com.suhailkandanur.fresco.util.FileUtils;
import com.suhailkandanur.fresco.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by suhail on 2016-12-02.
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {

    @Autowired
    private FrescoConfiguration configuration;

    @Autowired
    private RepositoryDAO repositoryDAO;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    @Override
    @Transactional
    public void processMessage(String message) throws IOException {
        Map<String, String> request = JsonUtils.convertStrToJson(message, HashMap.class);
        logger.info("received message: {}, object: {}", message, request);
        Optional<Repository> repositoryInDb = repositoryDAO.findRepositoryByName(request.get("name"));
        if (repositoryInDb.isPresent()) {
            logger.error("repository with name '{}' already present, cannot create a new one", request.get("name"));
            return;
        }
        Repository repository = new Repository(request.get("name"),
                request.get("description"),
                Long.valueOf(request.getOrDefault("quota", "1000000")));
        createRepository(repository);
        writeEntryToDatabase(repository);
    }

    void createRepository(Repository repository) throws IOException {
        Objects.requireNonNull(repository);
        Path fileSystemPath = Paths.get(configuration.getFileSystem());
        if (Files.notExists(fileSystemPath)) {
            logger.error("root file system path '{}' does not exist", fileSystemPath);
            return;
        }

        //TODO: lock the repository before continuing
        Path frescoRootPath = fileSystemPath.resolve("fresco");
        Files.createDirectories(frescoRootPath);

        Path repositoryRoot = frescoRootPath.resolve(repository.getName());
        Files.createDirectories(repositoryRoot);

        Path storesRootPath = repositoryRoot.resolve("stores");
        Files.createDirectories(storesRootPath);

        Path metaDataFile = repositoryRoot.resolve("meta.inf");
        if (Files.notExists(metaDataFile)) {
            Path metaDataFileTmp = repositoryRoot.resolve(".meta.inf.tmp");
            if(Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
            if (FileUtils.writeToFile(metaDataFileTmp, JsonUtils.convertObjectToJsonStr(repository)) != null) {
                Files.move(metaDataFileTmp, metaDataFile);
                if (Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
                logger.info("successfully created meta data file {}", metaDataFile);
            }
        }
    }

    void writeEntryToDatabase(Repository repository) {
        repositoryDAO.save(repository);
    }
}
