package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Created by suhail on 2016-12-02.
 */
public interface RepositoryDAO extends MongoRepository<Repository, Integer> {
    //Optional<Repository> findRepositoryById(int ref);
    Optional<Repository> findRepositoryByName(String name);

}
