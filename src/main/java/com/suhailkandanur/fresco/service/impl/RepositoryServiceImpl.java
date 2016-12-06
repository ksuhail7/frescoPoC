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

            Repository repository = JsonUtils.convertStrToJson(message, Repository.class);

            logger.info("received message: {}, object: {}", message, repository);
            Repository repositoryInDb = repositoryDAO.findFrescoRepoByName(repository.getName());
            if (repositoryInDb != null) {
                logger.error("repository with name '{}' already present, cannot create a new one", repository.getName());
                return;
            }


            repository.setRootPath(Paths.get(configuration.getFileSystem(), "fresco", repository.getName()).toString());

            createRepository(repository);
            writeEntryToDatabase(repository);
        } catch(IOException ioe) {
            logger.error("error processing request, message: {}", ioe.getMessage());
            return;
        }
    }

    boolean createRepository(Repository repository) throws Exception {
        Objects.requireNonNull(repository);


        //TODO: lock the repository before continuing
        //Boolean status = repository.<Boolean>withLockOn(() -> {
            try {
                Path repositoryRoot = Paths.get(repository.getRootPath());
                if (repositoryRoot != null && Files.notExists(repositoryRoot.getParent().getParent())) {
                    logger.error("root file system path '{}' does not exist", repositoryRoot.getParent().getParent());
                    return false;
                }

                if (Files.exists(repositoryRoot)) {
                    logger.error("repository root directory '{}' already exists, cannot create repository on filesystem",
                            repositoryRoot);
                    return false;
                }
                Files.createDirectories(repositoryRoot);

                Path storesRootPath = repositoryRoot.resolve("stores");
                Files.createDirectories(storesRootPath);

                //write meta.inf file
                FileUtils.writeMetaInfFile(repositoryRoot, repository);
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
