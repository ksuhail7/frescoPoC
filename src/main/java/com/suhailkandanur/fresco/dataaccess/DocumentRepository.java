package com.suhailkandanur.fresco.dataaccess;

import com.mongodb.Mongo;
import com.suhailkandanur.fresco.entity.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by suhail on 2016-12-05.
 */
public interface DocumentRepository extends MongoRepository<Document, String> {
}
