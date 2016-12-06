package com.suhailkandanur.fresco.dataaccess;

import com.suhailkandanur.fresco.entity.DocumentVersion;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by suhail on 2016-12-05.
 */
public interface DocumentVersionRepository extends MongoRepository<DocumentVersion, String> {
}
