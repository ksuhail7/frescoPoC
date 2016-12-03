package com.suhailkandanur.fresco.entity;

import java.io.Serializable;

/**
 * Created by suhail on 2016-12-01.
 */
public class Repository implements Serializable {
    private String name;
    private String description;
    private long quota;
    private boolean created;
    private String rootPath;


    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public Repository(String name, String description, long quota) {
        this.name = name;
        this.description = description;
        this.quota = quota;
    }

    public Repository() {

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getQuota() {
        return quota;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
