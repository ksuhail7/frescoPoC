package com.suhailkandanur.fresco.entity;

/**
 * Created by suhail on 2016-12-16.
 */
public class FileObjectReference {
    private String fileLocation;
    private String mimeType;

    public FileObjectReference(String fileLocation, String mimeType) {
        this.fileLocation = fileLocation;
        this.mimeType = mimeType;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getMimeType() {
        return mimeType;
    }
}
