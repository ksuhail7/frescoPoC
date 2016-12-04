package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.FrescoRepo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by suhail on 2016-12-03.
 */
public interface FrescoRepoRepository extends MongoRepository<FrescoRepo, String> {
}
