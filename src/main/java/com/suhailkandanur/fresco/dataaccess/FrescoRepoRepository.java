package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by suhail on 2016-12-03.
 */
@org.springframework.stereotype.Repository
public interface FrescoRepoRepository extends MongoRepository<Repository, String> {
    Repository findFrescoRepoByName(String name);
}
