package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.FrescoRepo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by suhail on 2016-12-03.
 */
@Repository
public interface FrescoRepoRepository extends MongoRepository<FrescoRepo, Integer> {
}
