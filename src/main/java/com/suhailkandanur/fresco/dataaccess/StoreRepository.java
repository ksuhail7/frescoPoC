package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by suhail on 2016-12-03.
 */
public interface StoreRepository extends MongoRepository<Store, String> {
    Store findStoreByToken(String token);

    List<Store> findStoreByRepositoryId(String repositoryId);
}
