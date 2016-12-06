package com.suhailkandanur.fresco.service.impl;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.dataaccess.FrescoRepoRepository;
import com.suhailkandanur.fresco.entity.Repository;
import com.suhailkandanur.fresco.service.RabbitQueueListener;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by suhail on 2016-12-02.
 */
@Service
public class RepositoryServiceImpl implements RabbitQueueListener {

    @Autowired
    private FrescoConfiguration configuration;

    @Autowired
    private FrescoRepoRepository repositoryDAO;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    @Override
    @Transactional
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "fresco-repository-request", durable = "true"), exchange = @Exchange(value = "fresco", type = "direct"), key = "repository"))
    public void processMessage(String message) throws Exception {
        try {
            Map<String, String> request = JsonUtils.convertStrToJson(message, HashMap.class);

            logger.info("received message: {}, object: {}", message, request);
            Optional<Repository> repositoryInDb = Optional.empty(); //repositoryDAO.findRepositoryByName(request.get("name"));
            if (repositoryInDb.isPresent()) {
                logger.error("repository with name '{}' already present, cannot create a new one", request.get("name"));
                return;
            }
            Repository repository = JsonUtils.convertStrToJson(message, Repository.class);
            createRepository(repository);
            writeEntryToDatabase(repository);
        } catch(IOException ioe) {
            logger.error("error processing request, message: {}", ioe.getMessage());
            return;
        }
    }

    boolean createRepository(Repository repository) throws Exception {
        Objects.requireNonNull(repository);
        Path fileSystemPath = Paths.get(configuration.getFileSystem());
        if (Files.notExists(fileSystemPath)) {
            logger.error("root file system path '{}' does not exist", fileSystemPath);
            return false;
        }

        //TODO: lock the repository before continuing
        //Boolean status = repository.<Boolean>withLockOn(() -> {
            try {
                Path frescoRootPath = fileSystemPath.resolve("fresco");
                if (Files.notExists(frescoRootPath)) Files.createDirectories(frescoRootPath);

                Path repositoryRoot = frescoRootPath.resolve(repository.getName());
                if (Files.exists(repositoryRoot)) {
                    logger.error("repository root directory '{}' already exists, cannot create repository on filesystem",
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
                    if (FileUtils.writeToFile(metaDataFileTmp, JsonUtils.convertObjectToJsonStr(repository)) != null) {
                        Files.move(metaDataFileTmp, metaDataFile);
                        if (Files.exists(metaDataFileTmp)) Files.delete(metaDataFileTmp);
                        logger.info("successfully created meta data file {}", metaDataFile);
                    }
                }
             return true;
            } catch (IOException ioe) {
                logger.error("error while creating repository, message: {}", ioe.getMessage());
                return false;
            }
        //});
    }

    void writeEntryToDatabase(Repository repository) {
        repositoryDAO.save(repository);
    }
}
