package com.suhailkandanur.fresco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by suhail on 2016-12-01.
 */
public class FrescoRepo {
    private String name;
    private String description;
    private long quota;
    private boolean created;
    private String rootPath;
    private String refToken;

    public FrescoRepo(String name, String description, long quota) {
        this.name = name;
        this.description = description;
        this.quota = quota;
    }

    public FrescoRepo() {

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public long getQuota() {
        return quota;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @JsonIgnore
    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getRefToken() {
        return refToken;
    }

    public void setRefToken(String refToken) {
        this.refToken = refToken;
    }

    public void setQuota(long quota) {
        this.quota = quota;
    }
}
