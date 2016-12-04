package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.entity.FrescoRepo;
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
    private FrescoRepoRepository repositoryDAO;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    @Override
    @Transactional
    public void processMessage(String message) throws Exception {
        Map<String, String> request = JsonUtils.convertStrToJson(message, HashMap.class);
        logger.info("received message: {}, object: {}", message, request);
        Optional<FrescoRepo> repositoryInDb = Optional.empty(); //repositoryDAO.findRepositoryByName(request.get("name"));
        if (repositoryInDb.isPresent()) {
            logger.error("frescoRepo with name '{}' already present, cannot create a new one", request.get("name"));
            return;
        }
        FrescoRepo frescoRepo = new FrescoRepo(request.get("name"),
                request.get("description"),
                Long.valueOf(request.getOrDefault("quota", "1000000")));
        createRepository(frescoRepo);
        writeEntryToDatabase(frescoRepo);
    }

    boolean createRepository(FrescoRepo frescoRepo) throws Exception {
        Objects.requireNonNull(frescoRepo);
        Path fileSystemPath = Paths.get(configuration.getFileSystem());
        if (Files.notExists(fileSystemPath)) {
            logger.error("root file system path '{}' does not exist", fileSystemPath);
            return false;
        }

        //TODO: lock the frescoRepo before continuing
        //Boolean status = frescoRepo.<Boolean>withLockOn(() -> {
            try {
                Path frescoRootPath = fileSystemPath.resolve("fresco");
                if (Files.notExists(frescoRootPath)) Files.createDirectories(frescoRootPath);

                Path repositoryRoot = frescoRootPath.resolve(frescoRepo.getName());
                if (Files.exists(repositoryRoot)) {
                    logger.error("frescoRepo root directory '{}' already exists, cannot create frescoRepo on filesystem",
                            repositoryRoot);
                    return false;
                }
                Files.createDirectories(repositoryRoot);

                Path storesRootPath = repositoryRoot.resolve("stores");
                Files.createDirectories(storesRootPath);

                Path metaDataFile = repositoryRoot.resolve("meta.inf");
                if (Files.notExists(metaDataFile)) {
                    Path metaDataFileTmp = repositoryRoot.resolve(".meta.inf.tmp");
                    if (Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
                    if (FileUtils.writeToFile(metaDataFileTmp, JsonUtils.convertObjectToJsonStr(frescoRepo)) != null) {
                        Files.move(metaDataFileTmp, metaDataFile);
                        if (Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
                        logger.info("successfully created meta data file {}", metaDataFile);
                    }
                }
             return true;
            } catch (IOException ioe) {
                logger.error("error while creating frescoRepo, message: {}", ioe.getMessage());
                return false;
            }
        //});
    }

    void writeEntryToDatabase(FrescoRepo repository) {
        repositoryDAO.save(repository);
    }
}
