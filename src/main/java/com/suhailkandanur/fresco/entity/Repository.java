package com.suhailkandanur.fresco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;

/**
 * Created by suhail on 2016-12-01.
 */
public class Repository {

    @Id
    private String id;
    private String name;
    private String description;
    private long quota;
    private boolean created;
    private String rootPath;
    private String token;

    public Repository(String name, String description, long quota) {
        this.name = name;
        this.description = description;
        this.quota = quota;
    }

    public Repository() {
        this.quota = 100_000_000L;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setQuota(long quota) {
        this.quota = quota;
    }

    public String getId() {
        return this.id;
    }
}
