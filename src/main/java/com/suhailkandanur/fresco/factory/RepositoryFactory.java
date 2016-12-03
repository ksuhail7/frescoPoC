package com.suhailkandanur.fresco.factory;

import com.suhailkandanur.fresco.configuration.FrescoConfiguration;
import com.suhailkandanur.fresco.dataaccess.RepositoryDAO;
import com.suhailkandanur.fresco.entity.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Created by suhail on 2016-12-02.
 */
public enum RepositoryFactory {

    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);

    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    private FrescoConfiguration frescoConfiguration;

    public static Repository getRepository(String name) {
        Optional<Repository> repositoryByName = INSTANCE.repositoryDAO.findRepositoryByName(name);
        if (repositoryByName.isPresent()) {
            return repositoryByName.get();
        }
        Repository repository = new Repository();
        repository.setName(name);
        repository.setCreated(false);
        return repository;
    }
}
