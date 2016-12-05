package com.suhailkandanur.fresco.entity;

import org.springframework.data.annotation.Id;

/**
 * Created by suhail on 2016-12-03.
 */
public class Store {
    @Id
    private int id;
    private String name;
    private String description;
    private int repositoryRef;
    private String repositoryRefToken;

    private Store(String name, String description, int repositoryRef, String repositoryRefToken) {
        this.name = name;
        this.description = description;
        this.repositoryRef = repositoryRef;
        this.repositoryRefToken = repositoryRefToken;
    }

    private Store(String name, String description, int repositoryRef) {
        this(name, description, repositoryRef, null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getRepositoryRef() {
        return repositoryRef;
    }

    public String getRepositoryRefToken() {
        return repositoryRefToken;
    }

    public static class StoreBuilder {
        private String repositoryRefToken;
        private String name;
        private String description;
        private int repositoryRef;

        public Store build() {
            return new Store(name, description, repositoryRef, repositoryRefToken);
        }

        public StoreBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StoreBuilder description(String description) {
            this.description = description;
            return this;
        }

        public StoreBuilder repositoryRef(int repositoryRef) {
            this.repositoryRef = repositoryRef;
            return this;
        }

        public StoreBuilder repositoryRefToken(String repositoryRefToken) {
            this.repositoryRefToken = repositoryRefToken;
            return this;
        }
    }
}
