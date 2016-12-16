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
    private String token;

    public Store(String name, String description, String repositoryId, String token) {
        this.name = name;
        this.description = description;
        this.repositoryId = repositoryId;
        this.token = token;
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

    public String getToken() {
        return token;
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

    public void setToken(String token) {
        this.token = token;
    }
}
