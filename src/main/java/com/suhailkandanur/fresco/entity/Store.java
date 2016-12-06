package com.suhailkandanur.fresco.entity;

import org.springframework.data.annotation.Id;

/**
 * Created by suhail on 2016-12-03.
 */
public class Store {

    @Id
    private String id;
    private String name;
    private String description;
    private String repositoryId;
    private String refToken;

    public Store(String name, String description, String repositoryId, String refToken) {
        this.name = name;
        this.description = description;
        this.repositoryId = repositoryId;
        this.refToken = refToken;
    }

    public Store(String name, String description, String repositoryId) {
        this(name, description, repositoryId, null);
    }

    public Store() {this(null, null, null);}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getRefToken() {
        return refToken;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public void setRefToken(String refToken) {
        this.refToken = refToken;
    }
}
