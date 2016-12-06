package com.suhailkandanur.fresco.entity;

import org.springframework.data.annotation.Id;

/**
 * Created by suhail on 2016-12-05.
 */
public class DocumentVersion {

    @Id
    private String id;

    private String documentId;
    private long version;
    private String filename;
    private long filesize;
    private String mimetype;
    private String sha1;
}
