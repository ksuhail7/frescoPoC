package com.suhailkandanur.fresco.entity;

import java.io.Serializable;

/**
 * Created by suhail on 2016-12-01.
 */
public class Repository implements Serializable {
    private String name;
    private String description;
    private long quota;

    public Repository(String name, String description, long quota) {
        this.name = name;
        this.description = description;
        this.quota = quota;
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
}
