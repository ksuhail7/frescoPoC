package com.suhailkandanur.fresco.service;

import com.suhailkandanur.fresco.entity.Document;
import com.suhailkandanur.fresco.entity.Repository;
import com.suhailkandanur.fresco.entity.Store;

/**
 * Created by suhail on 2016-12-09.
 */
public interface StorageService {
    String getRootPath(Repository repository);
    String getRootPath(Store store);
    String getRootPath(Document document);
}
